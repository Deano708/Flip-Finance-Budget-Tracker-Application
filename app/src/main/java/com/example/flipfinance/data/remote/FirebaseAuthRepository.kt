package com.example.flipfinance.data.remote

import com.example.flipfinance.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
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

class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
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

    override suspend fun register(email: String, pass: String): Result<Unit> = runCatching {
        firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
        Unit
    }

    override fun logout() = firebaseAuth.signOut()

    // Deletes the Firebase Auth account for the currently signed-in user.
    override suspend fun deleteAccount(): Result<Unit> = runCatching {
        firebaseAuth.currentUser?.delete()?.await()
            ?: throw IllegalStateException("No authenticated user")
        Unit
    }

    // Reset Password
    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = runCatching {
        firebaseAuth.sendPasswordResetEmail(email).await()
        Unit
    }
}