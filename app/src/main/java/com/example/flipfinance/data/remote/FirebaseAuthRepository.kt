package com.example.flipfinance.data.remote

import com.example.flipfinance.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.example.flipfinance.domain.model.User

/*
   Title: Adding in simple Firebase Authentication
   Author: ebAdamZA
   Date: 13 March 2026
   Date accessed: 18/04/2026
   Code version : 1
   Availability: https://github.com/PROG7313-2026-EMDBN/Sandbox.git
*/

/*
   Title: All about Firebase Authentication 🔥 | Login & Signup | Jetpack Compose
   Author: Easy Tuto
   Date: 1 year ago
   Date accessed: 18/04/2026
   Code version : 1
   Availability: https://youtu.be/KOnLpNZ4AFc?si=0_ykHIWXJTBMgkbb
*/

/*
   Title: How To Implement the Reset Password in Firebase using Android Studio and Kotlin
   Author: tutorialsEU
   Date: 5 year ago
   Date accessed: 23/04/2026
   Code version : 1
   Availability: https://youtu.be/nVhPqPpgndM?si=-2e5lkDbB83rUAoS
*/

//Title: Read and Write Data on Android
//Author: Firebase Documentation
//Date: 2024
//Date accessed: 29 April 2026
//Availability: https://firebase.google.com/docs/database/android/read-and-write

class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase
) : AuthRepository {

    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser?.let { User(uid = it.uid, email = it.email ?: "") }
            trySend(user)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun login(email: String, pass: String): Result<Unit> = runCatching {
        firebaseAuth.signInWithEmailAndPassword(email, pass).await()
        Unit
    }

    // Creates the Auth account then saves firstName/lastName to Realtime Database
    override suspend fun register(
        email: String,
        pass: String,
        firstName: String,
        lastName: String
    ): Result<Unit> = runCatching {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
        val uid = result.user?.uid ?: throw IllegalStateException("Registration failed — no user returned")

        // Save name fields to Database under users/{uid}
        firebaseDatabase.reference
            .child("users")
            .child(uid)
            .updateChildren(
                mapOf(
                    "firstName" to firstName.trim(),
                    "lastName" to lastName.trim()
                )
            ).await()
        Unit
    }

    override fun logout() = firebaseAuth.signOut()

    override suspend fun deleteAccount(): Result<Unit> = runCatching {
        firebaseAuth.currentUser?.delete()?.await()
            ?: throw IllegalStateException("No authenticated user")
        Unit
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = runCatching {
        firebaseAuth.sendPasswordResetEmail(email).await()
        Unit
    }

    // For Home
    override suspend fun getUserProfile(uid: String): Result<com.example.flipfinance.domain.model.UserProfile> = runCatching {
        // Reference to the specific user node in the Realtime Database
        val snapshot = firebaseDatabase.reference
            .child("users")
            .child(uid)
            .get()
            .await()

        // Map the database snapshot to UserProfile data class
        val firstName = snapshot.child("firstName").value as? String ?: ""
        val lastName = snapshot.child("lastName").value as? String ?: ""
        val email = firebaseAuth.currentUser?.email ?: ""

        com.example.flipfinance.domain.model.UserProfile(
            uid = uid,
            firstName = firstName,
            lastName = lastName,
            email = email
        )
    }
}