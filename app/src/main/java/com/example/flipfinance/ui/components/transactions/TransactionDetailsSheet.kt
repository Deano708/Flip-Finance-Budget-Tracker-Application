package com.example.flipfinance.ui.components.transactions

import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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
    var showReceipt by remember { mutableStateOf(false) }
    var showFullImage by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState()),
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
                value = formatTransactionDate(transaction.date),
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
                value = formatTransactionTime(transaction.date),
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

        // Receipt Section
        if (!transaction.receiptUrl.isNullOrEmpty()) {
            Text(
                text = "Attached Receipt",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )

            // Preview Image using Coil
            AsyncImage(
                model = transaction.receiptUrl,
                contentDescription = "Transaction Receipt",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Set a fixed height for the preview
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

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

        // Inside TransactionDetailsSheet
        if (transaction.receiptUrl != null) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Download Button
                Button(
                    onClick = {
                        downloadReceipt(context, transaction.receiptUrl, transaction.title)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB4D9BC)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Save", color = Color.Black)
                }
            }
        }
    }
}
fun formatTransactionDate(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return format.format(date)
}

fun formatTransactionTime(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    return format.format(date)
}