package com.example.flipfinance.ui.screens.Home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flipfinance.ViewModel.AuthViewModel
import com.example.flipfinance.ViewModel.SettingsViewModel
import com.example.flipfinance.ViewModel.TransactionViewModel
import com.example.flipfinance.ui.home.extractNameFromEmail
import com.example.flipfinance.ui.home.getGreeting
import com.example.flipfinance.ui.theme.FlipFinanceTheme
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

@Composable
fun HomeScreen(
    transactionViewModel: TransactionViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    onNavigateToAdd: () -> Unit,
    onNavigateToAnalytics: () -> Unit
) {
    // For Home
    val userProfile by authViewModel.userProfile.collectAsState()
    val firstName = userProfile?.firstName ?: "User"

    // Transaction Data
    val transactions by transactionViewModel.transactions.collectAsState()
    val totalSpent by transactionViewModel.totalSpentThisMonth.collectAsState()
    val topCategory by transactionViewModel.highestCategorySpend.collectAsState()
    val comparison by transactionViewModel.spendingComparison.collectAsState()

    // Settings Data
    val settingsState by settingsViewModel.uiState.collectAsState()

    // Extracting user preferences from settingsState
    val currencySymbol = settingsState.currency.symbol
    val userMaxBudget = settingsState.maxBudget.toDoubleOrNull() ?: 44500.0
    val isDarkMode = settingsState.isDarkMode

    val userEmail = FirebaseAuth.getInstance().currentUser?.email
    val userName = extractNameFromEmail(userEmail)
    val greeting = getGreeting()

    // Pass the isDarkMode preference into your Theme wrapper
    FlipFinanceTheme(darkTheme = isDarkMode) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onNavigateToAdd,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Transaction")
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }

                // 1. Greeting Section
                item {
                    GreetingSection(greeting, firstName)
                }

                // 2. Main Budget Card (Updated with dynamic currency and budget)
                item {
                    BudgetProgressCard(
                        totalSpent = totalSpent,
                        budget = userMaxBudget,
                        primaryColor = MaterialTheme.colorScheme.primary,
                        currencySymbol = currencySymbol
                    )
                }

                // 3. Insight Cards Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            title = "vs Last Month",
                            value = "${if (comparison >= 0) "+" else ""}${String.format(Locale.ENGLISH, "%.2f", comparison)}%",
                            icon = if (comparison <= 0) Icons.Default.TrendingDown else Icons.Default.TrendingUp,
                            iconColor = if (comparison <= 0) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                            onClick = onNavigateToAnalytics
                        )

                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            title = "Highest Spend",
                            value = topCategory?.first ?: "N/A",
                            // Displaying dynamic currency symbol
                            subValue = "$currencySymbol ${String.format(Locale.ENGLISH, "%.2f", topCategory?.second ?: 0.0)}",
                            icon = Icons.Default.PieChart,
                            iconColor = MaterialTheme.colorScheme.secondary,
                            onClick = onNavigateToAnalytics
                        )
                    }
                }

                // 4. Recent Transactions List
                item {
                    Text(
                        text = "Recent Transactions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(transactions.take(5)) { transaction ->
                    TransactionListItem(
                        transaction = transaction,
                        currencySymbol = currencySymbol // Pass symbol to list items
                    )
                }

                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}