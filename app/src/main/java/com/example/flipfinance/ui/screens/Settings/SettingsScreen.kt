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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flipfinance.Preferences.Settings.SupportedCurrency
import com.example.flipfinance.ViewModel.SettingsViewModel

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
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        // GROUP 1: Preferences
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Preferences", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dark Mode")
                    Switch(
                        checked = state.isDarkMode,
                        onCheckedChange = { viewModel.onDarkModeToggled(it) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Budget Alert Notifications")
                    Switch(
                        checked = state.budgetAlertsEnabled,
                        onCheckedChange = { viewModel.onBudgetAlertsToggled(it) }
                    )
                }
            }
        }

        // GROUP 2: Currency
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Currency", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))

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
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
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
        }

        // GROUP 3: Monthly Budget
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Monthly Budget Limits", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.minBudget,
                    onValueChange = { viewModel.onMinBudgetChanged(it) },
                    label = { Text("Minimum Budget") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Text(state.currency.symbol) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.maxBudget,
                    onValueChange = { viewModel.onMaxBudgetChanged(it) },
                    label = { Text("Maximum Budget") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Text(state.currency.symbol) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // GROUP 4: About & Links
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("About", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = { /* Navigate to Terms */ }) { Text("Terms of Service") }
                TextButton(onClick = { /* Navigate to Privacy */ }) { Text("Privacy Policy") }
                TextButton(onClick = { /* Navigate to Help */ }) { Text("Help & Support") }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "FlipFinance Version 1.0.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}