package com.example.flipfinance.ui.screens.Main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.flipfinance.data.local.Entities.Transaction
import com.example.flipfinance.ViewModel.TransactionViewModel
import com.example.flipfinance.data.local.components.TransactionItem

@Composable
fun TransactionScreen(viewModel: TransactionViewModel, onTransactionClick: (Transaction) -> Unit) {
    val transactionList by viewModel.transactions.collectAsState()

    Scaffold(
        topBar = { Text("My Transactions", style = MaterialTheme.typography.headlineMedium) }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(transactionList) { transaction ->
                TransactionItem(transaction = transaction, onClick = { onTransactionClick(transaction) })
            }
        }
    }
}