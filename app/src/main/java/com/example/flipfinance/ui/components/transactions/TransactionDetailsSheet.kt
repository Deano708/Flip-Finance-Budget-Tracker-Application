package com.example.flipfinance.ui.components.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.flipfinance.data.local.Entities.Transaction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color



@Composable
fun TransactionDetailsSheet(
    transaction: Transaction,
    onDelete: () -> Unit,
    onEdit: (Transaction) -> Unit
) {
    // variables with current transaction data
    var title by remember { mutableStateOf(transaction.title) }
    var amount by remember { mutableStateOf(transaction.amount.toString()) }
    var description by remember { mutableStateOf(transaction.description) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // expense and income Badge (at the top of the page)
        Surface(
            color = if (transaction.expenseType == "Expense") Color(0xFFFFC1C1) else Color(0xFFB4D9BC),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = transaction.expenseType,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Allows user to change transaction title.
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // allows them to alter amounts and the date of transaction
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = "R $amount",
                onValueChange = { amount = it.replace("R ", "") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = "01 Mar 2026", //(this needs to be edited)
                onValueChange = {},
                modifier = Modifier.weight(1f),
                enabled = false,
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Category Row
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = transaction.expenseCategory,
                onValueChange = {},
                modifier = Modifier.weight(1f),
                enabled = false,
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = "09:30",
                onValueChange = {},
                modifier = Modifier.weight(1f),
                enabled = false,
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Notes Area
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth().height(100.dp),
            shape = RoundedCornerShape(12.dp),
            label = { Text("Notes") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = onDelete,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE55A5A)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Delete")
            }
            Button(
                onClick = {
                    onEdit(transaction.copy(title = title, amount = amount.toDoubleOrNull() ?: 0.0, description = description))
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC75F)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Edit", color = Color.Black)
            }
        }
    }
}