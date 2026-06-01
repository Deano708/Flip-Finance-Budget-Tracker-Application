package com.example.flipfinance.domain.repository

import com.example.flipfinance.domain.model.User
import com.example.flipfinance.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

// Defines the Contract for Authentication without depending on Firebase SDKs
interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun login(email: String, pass: String): Result<Unit>
    suspend fun register(email: String, pass: String, firstName: String, lastName: String): Result<Unit>
    fun logout()
    suspend fun deleteAccount(): Result<Unit>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    // For Home
    suspend fun getUserProfile(uid: String): Result<UserProfile>
}