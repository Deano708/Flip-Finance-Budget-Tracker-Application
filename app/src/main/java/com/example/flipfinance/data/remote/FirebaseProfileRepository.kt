package com.example.flipfinance.data.remote

import android.net.Uri
import com.example.flipfinance.domain.model.UserProfile
import com.example.flipfinance.domain.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseProfileRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage
) : ProfileRepository {

    override val userProfile: Flow<UserProfile?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                trySend(null)
            } else {
                val parts = firebaseUser.displayName.orEmpty().trim().split(" ", limit = 2)
                val profile = UserProfile(
                    uid       = firebaseUser.uid,
                    firstName = parts.getOrElse(0) { "" },
                    lastName  = parts.getOrElse(1) { "" },
                    email     = firebaseUser.email.orEmpty(),
                    photoUrl  = firebaseUser.photoUrl?.toString()
                )
                trySend(profile)
            }
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun uploadPhoto(uri: Uri): Result<Unit> = runCatching {
        val uid = firebaseAuth.currentUser?.uid
            ?: throw IllegalStateException("No authenticated user")

        val ref = firebaseStorage.reference.child("avatars/$uid")
        ref.putFile(uri).await()
        val downloadUrl = ref.downloadUrl.await()

        val request = UserProfileChangeRequest.Builder()
            .setPhotoUri(downloadUrl)
            .build()
        firebaseAuth.currentUser!!.updateProfile(request).await()
        Unit
    }
}