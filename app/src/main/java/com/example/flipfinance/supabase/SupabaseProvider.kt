package com.example.flipfinance.supabase
import android.content.Context
import android.net.Uri
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class AvatarStorageService {

    private val bucketName = "avatars"

    suspend fun uploadAvatar(
        context: Context,
        imageUri: Uri
    ): String = withContext(Dispatchers.IO) {
        val userId = com.google.firebase.auth.FirebaseAuth
            .getInstance()
            .currentUser?.uid
            ?: throw IllegalStateException("User not logged in")

        val bytes = context.contentResolver
            .openInputStream(imageUri)
            ?.use { it.readBytes() }
            ?: throw IllegalStateException("Could not read selected image")

        val fileName = "${UUID.randomUUID()}.jpg"
        val path = "$userId/$fileName"

        SupabaseProvider.client.storage
            .from(bucketName)
            .upload(
                path = path,
                data = bytes
            ) {
                upsert = true
            }

        SupabaseProvider.client.storage
            .from(bucketName)
            .publicUrl(path)
    }
}