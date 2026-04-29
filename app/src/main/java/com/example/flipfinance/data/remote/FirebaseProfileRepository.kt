package com.example.flipfinance.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.example.flipfinance.domain.model.UserProfile
import com.example.flipfinance.domain.repository.ProfileRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
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
    @ApplicationContext private val context: Context
) : ProfileRepository {

    // Emits a fresh UserProfile whenever auth state changes.
    // Reads firstName/lastName from Realtime Database users/{uid}/
    override val userProfile: Flow<UserProfile?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                trySend(null)
            } else {
                firebaseDatabase.reference
                    .child("users")
                    .child(firebaseUser.uid)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val profile = UserProfile(
                            uid       = firebaseUser.uid,
                            firstName = snapshot.child("firstName").getValue(String::class.java) ?: "",
                            lastName  = snapshot.child("lastName").getValue(String::class.java) ?: "",
                            email     = firebaseUser.email.orEmpty(),
                            photoUrl  = snapshot.child("photoUrl").getValue(String::class.java)
                        )
                        trySend(profile)
                    }
                    .addOnFailureListener {
                        trySend(
                            UserProfile(
                                uid   = firebaseUser.uid,
                                email = firebaseUser.email.orEmpty()
                            )
                        )
                    }
            }
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    // Uploads photo as Base64 into Realtime Database
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

    // Updates firstName/lastName in Database and email/password in Firebase Auth.
    // Only updates fields that are non-blank — blank fields are left unchanged.
    // Re-authentication is required by Firebase before changing email or password.
    override suspend fun updateCredentials(
        firstName: String,
        lastName: String,

        password: String
    ): Result<Unit> = runCatching {
        val user = firebaseAuth.currentUser
            ?: throw IllegalStateException("No authenticated user")

        // Update name in Database if provided
        val nameUpdates = mutableMapOf<String, Any>()
        if (firstName.isNotBlank()) nameUpdates["firstName"] = firstName.trim()
        if (lastName.isNotBlank()) nameUpdates["lastName"] = lastName.trim()
        if (nameUpdates.isNotEmpty()) {
            firebaseDatabase.reference
                .child("users")
                .child(user.uid)
                .updateChildren(nameUpdates)
                .await()
        }

        // Update email in Auth if provided and different


        // Update password in Auth if provided
        if (password.isNotBlank()) {
            user.updatePassword(password).await()
        }

        Unit
    }

    private fun compressAndEncodeImage(uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open image URI")

        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

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

        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)

        if (scaledBitmap != originalBitmap) scaledBitmap.recycle()
        originalBitmap.recycle()

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }
}