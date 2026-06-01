package com.example.flipfinance.ui.screens.Settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
   Title: Layouts in Jetpack Compose
   Author: Android Developers Documentation
   Date: 2024
   Date accessed: 01/06/2026
   Availability: https://developer.android.com/develop/ui/compose/layouts
*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsOfServiceScreen(onNavigateBack: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Terms of Service",
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
                text = "Last updated: 1 June 2026",
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onSurfaceVariant
            )

            LegalSection(
                title = "1. Acceptance of Terms",
                body = "By downloading, installing, or using FlipFinance, you agree to be bound by these Terms of Service. " +
                        "If you do not agree to these terms, please do not use the application."
            )

            LegalSection(
                title = "2. Use of the Application",
                body = "FlipFinance is a personal finance tracking tool intended for personal, non-commercial use. " +
                        "You agree to use the app only for lawful purposes and in accordance with these terms. " +
                        "You are responsible for maintaining the confidentiality of your account credentials."
            )

            LegalSection(
                title = "3. Account Registration",
                body = "To use certain features of FlipFinance, you must register an account using a valid email address. " +
                        "You agree to provide accurate and complete information during registration and to keep " +
                        "your account information up to date."
            )

            LegalSection(
                title = "4. Data and Privacy",
                body = "Your transaction data is stored securely using Firebase Realtime Database and is associated " +
                        "with your authenticated account. We do not sell or share your personal financial data with " +
                        "third parties. Please refer to our Privacy Policy for full details on how your data is handled."
            )

            LegalSection(
                title = "5. Financial Disclaimer",
                body = "FlipFinance is a budgeting and expense tracking tool only. It does not constitute financial " +
                        "advice. The information and summaries presented in the app are based solely on the data " +
                        "you input and should not be used as the basis for financial decisions."
            )

            LegalSection(
                title = "6. Limitation of Liability",
                body = "To the fullest extent permitted by law, FlipFinance and its developers shall not be liable " +
                        "for any indirect, incidental, or consequential damages arising from your use of the " +
                        "application, including loss of data or financial loss."
            )

            LegalSection(
                title = "7. Changes to Terms",
                body = "We reserve the right to modify these Terms of Service at any time. Continued use of the " +
                        "application after changes constitutes your acceptance of the revised terms. " +
                        "We will notify users of significant changes where possible."
            )

            LegalSection(
                title = "8. Governing Law",
                body = "These terms are governed by and construed in accordance with the laws of South Africa. " +
                        "Any disputes arising from these terms shall be subject to the jurisdiction of " +
                        "South African courts."
            )

            LegalSection(
                title = "9. Contact",
                body = "If you have any questions about these Terms of Service, please contact us through the " +
                        "Help & Support section of the application."
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// Reusable section composable — shared across all three legal screens
@Composable
fun LegalSection(title: String, body: String) {
    val colorScheme = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurface
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
        )
    }
}