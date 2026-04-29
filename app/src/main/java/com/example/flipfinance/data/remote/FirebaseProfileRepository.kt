package com.example.flipfinance.data.remote

import android.content.Context
import android.net.Uri
import com.example.flipfinance.domain.model.UserProfile
import com.example.flipfinance.domain.repository.ProfileRepository
import com.example.flipfinance.supabase.AvatarStorageService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/*
   Title: Tutorial: Implement Firebase Realtime Database in your app using Jetpack Compose
   Author: Daniel Atitienei (YouTube)
   Date:  Nov 27, 2024
   Date accessed: 29/04/2026
   Availability: https://www.youtube.com/watch?v=9dODKQhGPm0
*/
class FirebaseProfileRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase,
    @ApplicationContext private val context: Context
) : ProfileRepository {

    private val avatarStorageService = AvatarStorageService()

    // Emits a fresh UserProfile whenever auth state changes.
    // Reads firstName, lastName and photoUrl from Realtime Database users/{uid}/
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

    // Uploads photo to Supabase Storage, then saves the returned public URL
    // to Firebase Realtime Database under users/{uid}/photoUrl
    override suspend fun uploadPhoto(uri: Uri): Result<Unit> = runCatching {
        val uid = firebaseAuth.currentUser?.uid
            ?: throw IllegalStateException("No authenticated user")

        // Upload to Supabase — returns a public URL string
        val publicUrl = avatarStorageService.uploadAvatar(context, uri)

        // Save the URL to Firebase Database
        firebaseDatabase.reference
            .child("users")
            .child(uid)
            .child("photoUrl")
            .setValue(publicUrl)
            .await()

        Unit
    }

    // Updates firstName/lastName in Database and password in Firebase Auth.
    // Only updates fields that are non-blank — blank fields are left unchanged.
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

        // Update password in Auth if provided
        if (password.isNotBlank()) {
            user.updatePassword(password).await()
        }

        Unit
    }
}