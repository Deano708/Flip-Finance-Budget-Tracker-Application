package com.example.flipfinance.ui.screens.Settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
   Title: Layouts in Jetpack Compose
   Author: Android Developers Documentation
   Date: 2024
   Date accessed: 01/06/2026
   Availability: https://developer.android.com/develop/ui/compose/layouts

   Title: Material Design 3 - Cards
   Author: Google
   Date: 2024
   Date accessed: 01/06/2026
   Availability: https://m3.material.io/components/cards/overview
*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpAndSupportScreen(onNavigateBack: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Help & Support",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.background,
                    titleContentColor = colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))




            Text(
                text = "Contact Us",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                border = BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.2f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ContactRow(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = "support@flipfinance.app"
                    )
                    HorizontalDivider(color = colorScheme.outlineVariant.copy(alpha = 0.2f))
                    ContactRow(
                        icon = Icons.Default.Schedule,
                        label = "Response Time",
                        value = "Within 2–3 business days"
                    )
                    HorizontalDivider(color = colorScheme.outlineVariant.copy(alpha = 0.2f))
                    ContactRow(
                        icon = Icons.Default.Info,
                        label = "Version",
                        value = "FlipFinance 1.0.0"
                    )
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// ── Reusable composables ──────────────────────────────────────────────────────



@Composable
private fun ContactRow(icon: ImageVector, label: String, value: String) {
    val colorScheme = MaterialTheme.colorScheme
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = colorScheme.onSurface
            )
        }
    }
}