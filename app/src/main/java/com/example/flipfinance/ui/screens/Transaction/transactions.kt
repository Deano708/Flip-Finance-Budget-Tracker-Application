package com.example.flipfinance.ui.screens.Transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flipfinance.data.local.Entities.Transaction
import com.example.flipfinance.ViewModel.TransactionViewModel
import com.example.flipfinance.data.local.components.TransactionItem
import com.example.flipfinance.ui.screens.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    viewModel: TransactionViewModel,
    navController: NavController, // Added NavController for navigation
    onTransactionClick: (Transaction) -> Unit
) {
    val transactionList by viewModel.transactions.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("My Transactions", style = MaterialTheme.typography.titleLarge)
                }
            )
        },
        floatingActionButton = {
            // This button navigates to your new Add Transaction screen
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddTransaction.route) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { padding ->
        if (transactionList.isEmpty()) {
            // Feedback for when the database is empty
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No transactions yet. Tap '+' to start!", color = MaterialTheme.colorScheme.outline)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(transactionList) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onClick = { onTransactionClick(transaction) }
                    )
                }
                // Extra space at the bottom so the FAB doesn't cover the last item
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}