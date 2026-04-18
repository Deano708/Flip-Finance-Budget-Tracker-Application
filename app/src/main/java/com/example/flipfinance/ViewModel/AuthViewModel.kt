package com.example.flipfinance.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flipfinance.domain.model.User
import com.example.flipfinance.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.Login -> performAction { repository.login(event.email, event.pass) }
            is AuthEvent.Register -> performAction { repository.register(event.email, event.pass) }
        }
    }

    private fun performAction(action: suspend () -> Result<Unit>) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            action()
                .onSuccess { _isAuthenticated.value = true }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }
}

sealed class AuthEvent {
    data class Login(val email: String, val pass: String) : AuthEvent()
    data class Register(val email: String, val pass: String) : AuthEvent()
}