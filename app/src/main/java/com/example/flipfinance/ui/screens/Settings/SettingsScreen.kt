package com.example.flipfinance.ui.screens.Settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flipfinance.Preferences.Settings.SupportedCurrency
import com.example.flipfinance.ViewModel.SettingsViewModel
import com.example.flipfinance.ui.components.settings.SettingsGroupCard
import com.example.flipfinance.ui.components.settings.SettingsTextButton
import com.example.flipfinance.ui.components.settings.SettingsTextField
import com.example.flipfinance.ui.components.settings.ToggleSettingRow

/*
Title: Disclosure of AI Usage in my Assessment.
• Section: SettingsScreen.
• AI Tool: Gemini
• Purpose/intention : Design and syntax implementation of SettingsScreen, including card designs for currency, budget and preferences.
• Date(s) 26/04/2026.
• https://gemini.google.com/share/71c6cb29b3c5
*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToTerms: () -> Unit = {},
    onNavigateToPrivacy: () -> Unit = {},
    onNavigateToHelp: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // GROUP 1: Preferences
            SettingsGroupCard(title = "Preferences") {
                ToggleSettingRow(
                    label = "Dark Mode",
                    checked = state.isDarkMode,
                    onCheckedChange = { viewModel.onDarkModeToggled(it) }
                )
                Divider(color = colorScheme.outlineVariant.copy(alpha = 0.2f))
                ToggleSettingRow(
                    label = "Budget Alert Notifications",
                    checked = state.budgetAlertsEnabled,
                    onCheckedChange = { viewModel.onBudgetAlertsToggled(it) }
                )
            }

            // GROUP 2: Currency
            SettingsGroupCard(title = "Currency") {
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = "${state.currency.displayName} (${state.currency.symbol})",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Currency") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        shape = MaterialTheme.shapes.large, // Using Shape.kt Large
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorScheme.secondary,
                            unfocusedBorderColor = colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        scrollState = rememberScrollState()
                    ) {
                        SupportedCurrency.entries.forEach { currency ->
                            DropdownMenuItem(
                                text = { Text("${currency.displayName} (${currency.symbol})") },
                                onClick = {
                                    viewModel.onCurrencySelected(currency)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }



            // GROUP 4: About & Links
            SettingsGroupCard(title = "About") {
                SettingsTextButton("Terms of Service") { onNavigateToTerms() }
                SettingsTextButton("Privacy Policy") { onNavigateToPrivacy() }
                SettingsTextButton("Help & Support") { onNavigateToHelp() }

                Text(
                    text = "FlipFinance Version 1.0.0",
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 12.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}