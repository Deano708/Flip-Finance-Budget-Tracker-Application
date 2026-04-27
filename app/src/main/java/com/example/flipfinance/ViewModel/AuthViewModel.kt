package com.example.flipfinance.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flipfinance.domain.model.User
import com.example.flipfinance.domain.repository.AuthRepository
import com.example.flipfinance.domain.util.AuthValidator
import com.example.flipfinance.domain.util.PasswordResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
   Title: State - Android Jetpack Compose - Part 6
   Author: Phillip Lackner
   Date: 5 years ago
   Date accessed: 18/04/2026
   Code version : 1
   Availability: https://youtu.be/s3m1PSd7VWc?si=W9D10o-CFGRSg9Ex
*/

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    val currentUser: Flow<User?> = repository.currentUser

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError = _passwordError.asStateFlow()

    // Reset User Password
    private val _resetEmailSent = MutableStateFlow(false)
    val resetEmailSent = _resetEmailSent.asStateFlow()

    // Account deleted — the screen observes this to navigate away
    private val _accountDeleted = MutableStateFlow(false)
    val accountDeleted = _accountDeleted.asStateFlow()

    // Clear errors when user inputs text after a failure
    fun onEmailChange() { _emailError.value = null }
    fun onPasswordChange() { _passwordError.value = null }

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.Login -> performAction(shouldAuthenticate = true) {
                repository.login(event.email, event.pass)
            }
            is AuthEvent.Register -> {
                if (validateInputs(event.email, event.pass)) {
                    performAction(shouldAuthenticate = true) {
                        repository.register(event.email, event.pass)
                    }
                }
            }
            is AuthEvent.ResetPassword -> {
                if (AuthValidator.isValidEmail(event.email)) {
                    performAction(shouldAuthenticate = false) {
                        val result = repository.sendPasswordResetEmail(event.email)
                        if (result.isSuccess) _resetEmailSent.value = true
                        result
                    }
                } else {
                    _emailError.value = "Please Enter a Valid Email Address"
                }
            }
            // Signs the user out — Firebase Auth state listener in MainActivity
            is AuthEvent.Logout -> repository.logout()

            // Deletes the Firebase Auth account — on success the auth state
            is AuthEvent.DeleteAccount -> performAction(shouldAuthenticate = false) {
                val result = repository.deleteAccount()
                if (result.isSuccess) _accountDeleted.value = true
                result
            }
        }
    }

    private fun validateInputs(email: String, pass: String): Boolean {
        val isEmailValid = AuthValidator.isValidEmail(email)
        val passwordResult = AuthValidator.validatePassword(pass)

        if (!isEmailValid) _emailError.value = "Please enter a Valid Email Address"

        if (passwordResult is PasswordResult.Invalid) {
            _passwordError.value = passwordResult.message
        }

        return isEmailValid && passwordResult is PasswordResult.Valid
    }

    private fun performAction(
        shouldAuthenticate: Boolean = false,
        action: suspend () -> Result<Unit>
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            action()
                .onSuccess {
                    if (shouldAuthenticate) {
                        _isAuthenticated.value = true
                    }
                }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun clearErrors() {
        _emailError.value = null
        _passwordError.value = null
        _error.value = null
    }
    fun resetAuthentication() {
        _isAuthenticated.value = false
    }
}

sealed class AuthEvent {
    data class Login(val email: String, val pass: String) : AuthEvent()
    data class Register(val email: String, val pass: String) : AuthEvent()
    data class ResetPassword(val email: String) : AuthEvent()
    data object Logout : AuthEvent()
    data object DeleteAccount : AuthEvent()
}