package com.example.flipfinance.ui.screens.Profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.flipfinance.ViewModel.ProfileEvent
import com.example.flipfinance.ViewModel.ProfileViewModel
import com.example.flipfinance.ui.theme.ErrorRed
import java.io.File

/*
   Title: Profile Screen with Image Upload
   Author: ebadamZA
   Date: 13 March 2026
   Date accessed: 29/04/2026
   Availability: https://github.com/PROG7313-2026-EMDBN/Sandbox.git
*/

/*
   Title: THIS Is How You Use the New Android 13 Photo Picker (It's AMAZING!)
   Author: Phillip Lackner
   Date: Dec 14, 2022
   Date accessed: 27/04/2026
   Availability: https://www.youtube.com/watch?v=uHX5NB6wHao
*/


@Composable
fun ProfileScreen(
    onNavigateToChangeCredentials: () -> Unit,
    onDeleteAccount: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    val isUploading by viewModel.isUploading.collectAsState()
    val uploadError by viewModel.uploadError.collectAsState()
    val uploadSuccess by viewModel.uploadSuccess.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    // Gallery picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onEvent(ProfileEvent.UploadPhoto(it)) }
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri?.let { viewModel.onEvent(ProfileEvent.UploadPhoto(it)) }
        }
    }

    fun createCameraUri(): Uri {
        val cameraDir = File(context.cacheDir, "camera").apply { mkdirs() }
        val photoFile = File.createTempFile("photo_", ".jpg", cameraDir)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uploadError) {
        uploadError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(ProfileEvent.ClearUploadError)
        }
    }
    LaunchedEffect(uploadSuccess) {
        if (uploadSuccess) {
            snackbarHostState.showSnackbar("Photo updated successfully!")
            viewModel.onEvent(ProfileEvent.ClearUploadSuccess)
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Delete Account", fontWeight = FontWeight.SemiBold) },
            text = { Text("This will permanently delete your account and all your data. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDeleteAccount()
                }) {
                    Text("Delete", color = ErrorRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Log Out", fontWeight = FontWeight.SemiBold) },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text("Log Out", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Header ───────────────────────────────────────────────────
            Surface(
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // ── Avatar ───────────────────────────────────────────
                    Box(contentAlignment = Alignment.Center) {
                        val photoUrl = userProfile?.photoUrl
                        if (photoUrl != null) {
                            // Load image from Supabase public URL using Coil
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = "Profile photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            // Initials fallback
                            val initials = buildString {
                                userProfile?.firstName?.firstOrNull()?.let { append(it.uppercaseChar()) }
                                userProfile?.lastName?.firstOrNull()?.let { append(it.uppercaseChar()) }
                            }.ifEmpty { "?" }
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = initials,
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        if (isUploading) {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(28.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.5.dp
                                )
                            }
                        }
                    }

                    Text(
                        text = "Update your profile photo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )

                    // ── Photo Buttons ────────────────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val uri = createCameraUri()
                                cameraImageUri = uri
                                cameraLauncher.launch(uri)
                            },
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.small,
                            enabled = !isUploading
                        ) {
                            Icon(Icons.Default.CameraAlt, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Take Photo", fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.small,
                            enabled = !isUploading
                        ) {
                            Icon(Icons.Default.Upload, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Upload Photo", fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Profile Info Card ────────────────────────────────────────
            SectionLabel("Profile Info")
            Spacer(Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                ProfileField(label = "First Name", value = userProfile?.firstName ?: "—")
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ProfileField(label = "Last Name", value = userProfile?.lastName ?: "—")
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ProfileField(label = "Email Address", value = userProfile?.email ?: "—")
            }

            Spacer(Modifier.height(20.dp))

            // ── Account Actions Card ─────────────────────────────────────
            SectionLabel("Account")
            Spacer(Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                ActionRow(
                    label = "Change Credentials",
                    icon = Icons.Default.Lock,
                    iconTint = MaterialTheme.colorScheme.primary,
                    onClick = onNavigateToChangeCredentials
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ActionRow(
                    label = "Delete Account",
                    icon = Icons.Default.Delete,
                    iconTint = ErrorRed,
                    labelColor = ErrorRed,
                    onClick = { showDeleteDialog = true }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ActionRow(
                    label = "Log Out",
                    icon = Icons.Default.Logout,
                    iconTint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    onClick = { showLogoutDialog = true }
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Reusable Composables ──────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
        letterSpacing = 1.sp,
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

@Composable
private fun ProfileField(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Read only",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun ActionRow(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    labelColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconTint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = iconTint, modifier = Modifier.size(16.dp))
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = labelColor,
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}