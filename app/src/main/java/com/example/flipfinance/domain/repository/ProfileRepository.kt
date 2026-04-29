package com.example.flipfinance.domain.repository

import android.net.Uri
import com.example.flipfinance.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    val userProfile: Flow<UserProfile?>
    suspend fun uploadPhoto(uri: Uri): Result<Unit>
    suspend fun updateCredentials(
        firstName: String,
        lastName: String,

        password: String
    ): Result<Unit>
}