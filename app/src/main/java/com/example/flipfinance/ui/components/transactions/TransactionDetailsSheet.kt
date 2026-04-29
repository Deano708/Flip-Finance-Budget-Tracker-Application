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
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/*
   Title: Tutorial: The FULL Beginner Guide for Room in Android | Local Database Tutorial for Android
   Author: Philipp Lackner (YouTube)
   Date: 15 March 2023
   Date accessed: 24/04/2026
   Availability: https://www.youtube.com/watch?v=bOd3wO0uFr8
*/

/*
   Title: Save data in a local database using Room
   Author: Android Developers
   Date: 5 March 2026
   Date accessed: 24/04/2026
   Availability: https://developer.android.com/training/data-storage/room
*/

/*
   Title: Accessing data using Room DAOs
   Author: Android Developers
   Date: 5 March 2026
   Date accessed: 24/04/2026
   Availability: https://developer.android.com/training/data-storage/room/accessing-data
*/

/*
   Title: Use Supabase with Android Kotlin
   Author: Supabase
   Date: 26 April 2026
   Date accessed: 26/04/2026
   Availability: https://supabase.com/docs/guides/getting-started/quickstarts/kotlin
*/

/*
   Title: Storage | Supabase | Jetpack Compose | Tutorial | 2023
   Author: YoursSohail
   Date: 23 October 2023
   Date accessed: 26/04/2026
   Availability: https://www.youtube.com/watch?v=BqxI7ViS_-M
*/

/*
   Title: Card
   Author: Android Developers
   Date: 24 April 2026
   Date accessed: 24/04/2026
   Availability: https://developer.android.com/develop/ui/compose/components/card
*/

/*
   Title: how can i make use of jetpack compose to make a dropdown of options from a database
   Author: Microsoft Copilot
   Date: 26 April 2026
   Code Version: 1
   Availability: https://copilot.microsoft.com/shares/4kNf4Zpv4nXXgkE23uoCJ
*/


@Composable
fun TransactionDetailsSheet(
    transaction: Transaction,
    onDelete: () -> Unit,
    currencySymbol: String,
    onEdit: (Transaction) -> Unit
) {
    // variables with current transaction data
    var title by remember { mutableStateOf(transaction.title) }
    var amount by remember { mutableStateOf(transaction.amount.toString()) }
    var description by remember { mutableStateOf(transaction.description) }
    var showReceipt by remember { mutableStateOf(false) }
    var showFullImage by remember { mutableStateOf(false) }

    val colorScheme = MaterialTheme.colorScheme

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
        val isExpense = transaction.expenseType == "Expense"
        Surface(
            color = if (isExpense) colorScheme.errorContainer else colorScheme.primary,
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = transaction.expenseType,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (isExpense) colorScheme.onError else colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Allows user to change transaction title.
        DetailsTextField(
            value = title,
            onValueChange = { title = it },
            label = "Transaction Title"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // allows them to alter amounts and the date of transaction
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DetailsTextField(
                value = "$currencySymbol $amount",
                onValueChange = { amount = it.replace("$currencySymbol ", "") },
                modifier = Modifier.weight(1f),
                label = "Amount"
            )
            DetailsTextField(
                value = formatTransactionDate(transaction.date),
                onValueChange = {},
                modifier = Modifier.weight(1f),
                enabled = false,
                label = "Date"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Category to allow the user to pick transaction category
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DetailsTextField(
                value = transaction.expenseCategory,
                onValueChange = {},
                modifier = Modifier.weight(1f),
                enabled = false,
                label = "Category"
            )
            DetailsTextField(
                value = formatTransactionTime(transaction.date),
                onValueChange = {},
                modifier = Modifier.weight(1f),
                enabled = false,
                label = "Time"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Notes Area
        DetailsTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            label = "Notes",
            singleLine = false
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Receipt Section
        if (!transaction.receiptUrl.isNullOrEmpty()) {
            Text(
                text = "Attached Receipt",
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )

            // Preview Image from supabase
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
            // Delete Button
            Button(
                onClick = onDelete,
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.errorContainer,
                    contentColor = colorScheme.onErrorContainer
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Delete", fontWeight = FontWeight.Bold)
            }

            // Edit Button
            Button(
                onClick = {
                    onEdit(transaction.copy(
                        title = title,
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        description = description
                    ))
                },
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.secondary,
                    contentColor = colorScheme.onSecondary
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Update", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

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

// Methods to set the time stamp in the details page to the time that the transaction was logged
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