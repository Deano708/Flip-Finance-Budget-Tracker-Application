package com.example.flipfinance.ui.screens.Profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flipfinance.ViewModel.ProfileEvent
import com.example.flipfinance.ViewModel.ProfileViewModel
import com.example.flipfinance.ui.components.PrimaryButton
import com.example.flipfinance.ui.components.PrimaryTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeCredentialsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isUpdating by viewModel.isUpdating.collectAsState()
    val updateError by viewModel.updateError.collectAsState()
    val updateSuccess by viewModel.updateSuccess.collectAsState()

    // Pre-fill fields with current values
    var firstName by remember(userProfile) { mutableStateOf(userProfile?.firstName ?: "") }
    var lastName by remember(userProfile) { mutableStateOf(userProfile?.lastName ?: "") }
    var email by remember(userProfile) { mutableStateOf(userProfile?.email ?: "") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(updateError) {
        updateError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(ProfileEvent.ClearUpdateError)
        }
    }

    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            snackbarHostState.showSnackbar("Credentials updated successfully!")
            viewModel.onEvent(ProfileEvent.ClearUpdateSuccess)
            onNavigateBack()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Change Credentials",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Update your details below. Leave a field unchanged to keep its current value.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(Modifier.height(8.dp))

            PrimaryTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = "First Name"
            )

            PrimaryTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = "Last Name"
            )

            PrimaryTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email"
            )

            PrimaryTextField(
                value = password,
                onValueChange = { password = it },
                label = "New Password",
                isPassword = true
            )

            PrimaryTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError = null
                },
                label = "Confirm New Password",
                isPassword = true,
                errorText = confirmPasswordError
            )

            Spacer(Modifier.height(8.dp))

            PrimaryButton(
                text = "Update Credentials",
                isLoading = isUpdating,
                onClick = {
                    // Validate passwords match if a new password was entered
                    if (password.isNotBlank() && password != confirmPassword) {
                        confirmPasswordError = "Passwords do not match"
                        return@PrimaryButton
                    }
                    viewModel.onEvent(
                        ProfileEvent.UpdateCredentials(
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            password = password
                        )
                    )
                }
            )
        }
    }
}