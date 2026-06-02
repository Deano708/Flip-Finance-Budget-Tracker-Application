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



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onNavigateBack: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Privacy Policy",
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
                title = "1. Introduction",
                body = "FlipFinance is committed to protecting your personal information. This Privacy Policy explains " +
                        "what data we collect, how we use it, and your rights regarding that data. By using " +
                        "FlipFinance, you agree to the practices described in this policy."
            )

            LegalSection(
                title = "2. Information We Collect",
                body = "We collect the following information when you use FlipFinance:\n\n" +
                        "• Account information: your first name, last name, and email address provided at registration.\n\n" +
                        "• Transaction data: titles, amounts, dates, categories, expense types, notes, and optional receipt images that you manually enter.\n\n" +
                        "• App usage data: the dates on which you open the application, stored locally on your device to power the engagement streak feature."
            )

            LegalSection(
                title = "3. How We Use Your Information",
                body = "Your data is used exclusively to provide and improve the FlipFinance experience:\n\n" +
                        "• To display your personal finance summaries, budgets, and transaction history.\n\n" +
                        "• To calculate streaks and achievement progress within the app.\n\n" +
                        "• To allow you to manage and update your account credentials."
            )

            LegalSection(
                title = "4. Data Storage",
                body = "Your account information and transaction data are stored securely in Google Firebase Realtime " +
                        "Database, protected by Firebase Authentication. Receipt images are stored in Supabase " +
                        "Storage. App-open tracking data and preferences are stored locally on your device using " +
                        "Android DataStore and are never transmitted to external servers."
            )

            LegalSection(
                title = "5. Data Sharing",
                body = "We do not sell, trade, or rent your personal information to third parties. Your data is " +
                        "not shared with advertisers or analytics platforms. The only third-party services used " +
                        "are Firebase (Google) and Supabase, whose own privacy policies govern their handling " +
                        "of infrastructure data."
            )

            LegalSection(
                title = "6. Data Retention and Deletion",
                body = "Your data is retained for as long as your account is active. You may delete your account " +
                        "at any time from the Profile screen, which will permanently remove your authentication " +
                        "record. You are encouraged to manually clear your Firebase data prior to account deletion " +
                        "if you wish to ensure full removal."
            )

            LegalSection(
                title = "7. Security",
                body = "We implement reasonable technical measures to protect your data, including Firebase " +
                        "Authentication for access control and encrypted transmission via HTTPS. However, no " +
                        "method of electronic storage or transmission is 100% secure, and we cannot guarantee " +
                        "absolute security."
            )

            LegalSection(
                title = "8. Children's Privacy",
                body = "FlipFinance is not intended for use by children under the age of 13. We do not knowingly " +
                        "collect personal information from children. If you believe a child has provided us with " +
                        "personal data, please contact us so we can take appropriate action."
            )

            LegalSection(
                title = "9. Changes to This Policy",
                body = "We may update this Privacy Policy from time to time. We will notify users of material " +
                        "changes where reasonably possible. Continued use of the app after changes take effect " +
                        "constitutes acceptance of the revised policy."
            )

            LegalSection(
                title = "10. Contact",
                body = "If you have any questions or concerns about this Privacy Policy or your data, please " +
                        "reach out through the Help & Support section of the application."
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}