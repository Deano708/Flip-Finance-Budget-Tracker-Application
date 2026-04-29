package com.example.flipfinance.domain.util

object AuthValidator {
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePassword(password: String): PasswordResult {
        return when {
            password.length < 8 -> PasswordResult.Invalid("Password must be at least 8 Characters")
            !password.any { it.isDigit() } -> PasswordResult.Invalid("Password must contain at least One Numerical Value")
            else -> PasswordResult.Valid
        }
    }
}

sealed class PasswordResult {
    object Valid : PasswordResult()
    data class Invalid(val message: String) : PasswordResult()
}