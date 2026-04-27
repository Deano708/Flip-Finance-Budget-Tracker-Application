package com.example.flipfinance.ViewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flipfinance.domain.model.UserProfile
import com.example.flipfinance.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    val userProfile = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isUploading = MutableStateFlow(false)
    val isUploading = _isUploading.asStateFlow()

    private val _uploadError = MutableStateFlow<String?>(null)
    val uploadError = _uploadError.asStateFlow()

    private val _uploadSuccess = MutableStateFlow(false)
    val uploadSuccess = _uploadSuccess.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating = _isUpdating.asStateFlow()

    private val _updateError = MutableStateFlow<String?>(null)
    val updateError = _updateError.asStateFlow()

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess = _updateSuccess.asStateFlow()

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.UploadPhoto -> uploadPhoto(event.uri)
            is ProfileEvent.ClearUploadError -> _uploadError.value = null
            is ProfileEvent.ClearUploadSuccess -> _uploadSuccess.value = false
            is ProfileEvent.UpdateCredentials -> updateCredentials(
                event.firstName, event.lastName, event.email, event.password
            )
            is ProfileEvent.ClearUpdateError -> _updateError.value = null
            is ProfileEvent.ClearUpdateSuccess -> _updateSuccess.value = false
        }
    }

    private fun uploadPhoto(uri: Uri) {
        viewModelScope.launch {
            _isUploading.value = true
            _uploadError.value = null
            repository.uploadPhoto(uri)
                .onSuccess { _uploadSuccess.value = true }
                .onFailure { _uploadError.value = it.message }
            _isUploading.value = false
        }
    }

    private fun updateCredentials(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            _isUpdating.value = true
            _updateError.value = null
            repository.updateCredentials(firstName, lastName, email, password)
                .onSuccess { _updateSuccess.value = true }
                .onFailure { _updateError.value = it.message }
            _isUpdating.value = false
        }
    }
}

sealed class ProfileEvent {
    data class UploadPhoto(val uri: Uri) : ProfileEvent()
    data object ClearUploadError : ProfileEvent()
    data object ClearUploadSuccess : ProfileEvent()
    data class UpdateCredentials(
        val firstName: String,
        val lastName: String,
        val email: String,
        val password: String
    ) : ProfileEvent()
    data object ClearUpdateError : ProfileEvent()
    data object ClearUpdateSuccess : ProfileEvent()
}