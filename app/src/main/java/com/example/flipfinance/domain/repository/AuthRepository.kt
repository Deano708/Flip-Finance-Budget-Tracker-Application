package com.example.flipfinance.domain.repository

import com.example.flipfinance.domain.model.User
import kotlinx.coroutines.flow.Flow

// Defines the Contract for Authentication without depending on Firebase SDKs
interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun login(email: String, pass: String): Result<Unit>
    suspend fun register(email: String, pass: String): Result<Unit>
    fun logout()
    suspend fun deleteAccount(): Result<Unit>

    // Reset User Password
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
}