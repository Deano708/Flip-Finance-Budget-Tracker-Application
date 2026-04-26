package com.example.flipfinance.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.example.flipfinance.domain.model.UserProfile
import com.example.flipfinance.domain.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import androidx.core.graphics.scale

class FirebaseProfileRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase,
    @ApplicationContext private val context: Context) : ProfileRepository {
    override val userProfile: Flow<UserProfile?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                trySend(null)
            } else {
                val parts = firebaseUser.displayName.orEmpty().trim().split(" ", limit = 2)

                firebaseDatabase.reference
                    .child("users")
                    .child(firebaseUser.uid)
                    .child("photoUrl")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val profile = UserProfile(
                            uid       = firebaseUser.uid,
                            firstName = parts.getOrElse(0) { "" },
                            lastName  = parts.getOrElse(1) { "" },
                            email     = firebaseUser.email.orEmpty(),
                            photoUrl  = snapshot.getValue(String::class.java)
                        )
                        trySend(profile)
                    }
                    .addOnFailureListener {

                        val profile = UserProfile(
                            uid       = firebaseUser.uid,
                            firstName = parts.getOrElse(0) { "" },
                            lastName  = parts.getOrElse(1) { "" },
                            email     = firebaseUser.email.orEmpty(),
                            photoUrl  = null
                        )
                        trySend(profile)
                    }
            }
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun uploadPhoto(uri: Uri): Result<Unit> = runCatching {
        val uid = firebaseAuth.currentUser?.uid
            ?: throw IllegalStateException("No authenticated user")

        val base64String = compressAndEncodeImage(uri)

        firebaseDatabase.reference
            .child("users")
            .child(uid)
            .child("photoUrl")
            .setValue(base64String)
            .await()

        Unit
    }

    private fun compressAndEncodeImage(uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open image URI")

        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        //Scale down to max 400x400 while keeping aspect ratio
        val maxSize = 400
        val scale = minOf(
            maxSize.toFloat() / originalBitmap.width,
            maxSize.toFloat() / originalBitmap.height,
            1f
        )

        val scaledBitmap = originalBitmap.scale(
            (originalBitmap.width * scale).toInt(),
            (originalBitmap.height * scale).toInt()
        )

        //Compress to JPEG at 60% quality,keeping Base64  under 1MB for most photos
        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)

        //Clean up bitmaps from memory
        if (scaledBitmap != originalBitmap) scaledBitmap.recycle()
        originalBitmap.recycle()

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }
}