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
) {
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

    val activeMinBudget = settingsState.minBudget.toDoubleOrNull() ?: 0.0
    val activeMaxBudget = settingsState.maxBudget.toDoubleOrNull() ?: 0.0

    val currentIncome = financeSummary.first
    val currentExpenses = remember(transactions) {
        transactions
            .filter { it.expenseType == "Expense" } // Matches your TransactionListItem logic
            .sumOf { it.amount }
    }

    val greeting = getGreeting()

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

                // 2. Main Budget Card
                item {
                    BudgetProgressCard(
                        totalSpent = currentExpenses,
                        budget = activeMaxBudget,
                        currencySymbol = currencySymbol,
                        minBudget = activeMinBudget,
                        maxBudget = activeMaxBudget,
                        income = currentIncome,
                        expenses = currentExpenses
                    )
                }

                // 3. Insight Cards Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DynamicBudgetCard(
                            modifier = Modifier.weight(1f),
                            currencySymbol = currencySymbol,
                            currentExpenses = currentExpenses,
                            minBudget = activeMinBudget,
                            maxBudget = activeMaxBudget,
                            icon = Icons.Default.TrendingDown,
                            iconColor = Color(0xFFFFB703), // Amber/Gold
                            onBudgetsSaved = { newMin, newMax ->
                                settingsViewModel.onMinBudgetChanged(newMin.toString())
                                settingsViewModel.onMaxBudgetChanged(newMax.toString())
                            }
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