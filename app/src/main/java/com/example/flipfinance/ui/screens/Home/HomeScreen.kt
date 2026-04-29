package com.example.flipfinance.ui.screens.Home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.flipfinance.ViewModel.TransactionViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flipfinance.ui.home.extractNameFromEmail
import com.example.flipfinance.ui.home.getGreeting
import com.example.flipfinance.ui.theme.FlipFinanceTheme
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    viewModel: TransactionViewModel = hiltViewModel(),
    onNavigateToAdd: () -> Unit,
    onNavigateToAnalytics: () -> Unit
) {
    // Observing the transformed data from ViewModel
    val transactions by viewModel.transactions.collectAsState()
    val totalSpent by viewModel.totalSpentThisMonth.collectAsState()
    val topCategory by viewModel.highestCategorySpend.collectAsState()
    val comparison by viewModel.spendingComparison.collectAsState()

    val userEmail = FirebaseAuth.getInstance().currentUser?.email
    val userName = extractNameFromEmail(userEmail)
    val greeting = getGreeting()

    FlipFinanceTheme { // Applying your custom theme
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
                    GreetingSection(greeting, userName)
                }

                // 2. Main Budget Card
                item {
                    BudgetProgressCard(
                        totalSpent = totalSpent,
                        budget = 44500.0,
                        primaryColor = MaterialTheme.colorScheme.primary
                    )
                }

                // 3. Insight Cards Row (vs Last Month & Top Category)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // vs Last Month Card
                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            title = "vs Last Month",
                            value = "${if (comparison >= 0) "+" else ""}${String.format("%.2f", comparison)}%",
                            icon = if (comparison <= 0) Icons.Default.TrendingDown else Icons.Default.TrendingUp,
                            iconColor = if (comparison <= 0) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                            onClick = onNavigateToAnalytics
                        )

                        // Highest Category Card
                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            title = "Highest Spend",
                            value = topCategory?.first ?: "N/A",
                            subValue = "R ${String.format("%.2f", topCategory?.second ?: 0.0)}",
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
                    TransactionListItem(transaction)
                }

                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}