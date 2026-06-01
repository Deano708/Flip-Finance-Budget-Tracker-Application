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
)
{
    val userProfile by authViewModel.userProfile.collectAsState()
    val firstName = userProfile?.firstName ?: "User"

    // Transaction Data Streams
    val transactions by transactionViewModel.transactions.collectAsState()
    val categoryList by transactionViewModel.categories.collectAsState()
    val totalSpent by transactionViewModel.totalSpentThisMonth.collectAsState()
    val topCategory by transactionViewModel.highestCategorySpend.collectAsState()
    val comparison by transactionViewModel.spendingComparison.collectAsState()
    val financeSummary by transactionViewModel.financeSummary.collectAsState()

    // Settings Stream
    val settingsState by settingsViewModel.uiState.collectAsState()
    val currencySymbol = settingsState.currency.symbol
    val isDarkMode = settingsState.isDarkMode

    val parsedMinBudget = settingsState.minBudget.toDoubleOrNull() ?: 0.0
    val parsedMaxBudget = settingsState.maxBudget.toDoubleOrNull() ?: 0.0

    val currentIncome = financeSummary.first
    val currentExpenses = totalSpent

    val userEmail = FirebaseAuth.getInstance().currentUser?.email
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

                // 2. Main Budget Card (Passing down pure limits and total monthly expense)
                item {
                    BudgetProgressCard(
                        totalSpent = currentExpenses,
                        budget = parsedMaxBudget,
                        currencySymbol = currencySymbol,
                        minBudget = parsedMinBudget, // Light Blue Pillar value
                        maxBudget = parsedMaxBudget, // Dark Blue Pillar value
                        income = currentIncome,      // Green Pillar value
                        expenses = currentExpenses   // Pink Pillar value
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
                        currencySymbol = currencySymbol,
                        categories = categoryList
                    )
                }

                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}