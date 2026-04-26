package com.example.flipfinance.data.local.components

import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.flipfinance.data.local.Entities.Transaction

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
   Title: Card
   Author: Android Developers
   Date: 24 April 2026
   Date accessed: 24/04/2026
   Availability: https://developer.android.com/develop/ui/compose/components/card
*/

@Composable
fun TransactionItem(transaction: Transaction, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        // Column to allow users to input their data.
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