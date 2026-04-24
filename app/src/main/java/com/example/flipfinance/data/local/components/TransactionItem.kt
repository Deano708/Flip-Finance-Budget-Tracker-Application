package com.example.flipfinance.data.local.components

import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.flipfinance.data.local.Entities.Transaction

@Composable
fun TransactionItem(transaction: Transaction, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = transaction.title, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "R${transaction.amount}",
                    color = if (transaction.expenseType == "Income") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
            Text(text = transaction.expenseCategory, style = MaterialTheme.typography.bodyMedium)
            Text(text = transaction.description, style = MaterialTheme.typography.bodySmall)
        }
    }
}