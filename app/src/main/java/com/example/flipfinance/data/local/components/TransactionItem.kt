package com.example.flipfinance.data.local.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp), // Subtle elevation for a "flat" modern look
        border = BorderStroke(1.dp, Color(0xFFF1F1F1))
    ) {
            Row(modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {

                // Category Icon
                Surface(
                    modifier = Modifier.size(44.dp),
                    color = if (transaction.expenseType == "Income") Color(0xFFB4D9BC) else Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = getCategoryIcon(transaction.expenseCategory),
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp),
                        tint = if (transaction.expenseType == "Income") Color(0xFF2E7D32) else Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Title and Category Label
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = transaction.expenseCategory,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                }

                // Amount and Type Indicator
                Column(horizontalAlignment = Alignment.End) {
                    val isIncome = transaction.expenseType == "Income"
                    Text(
                        text = "${if (isIncome) "+" else "-"} R${String.format("%.2f", transaction.amount)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isIncome) Color(0xFF2E7D32) else Color.Black
                    )
                    // Status Tag for Descriptions
                    if (transaction.description.isNotBlank()) {
                        Icon(
                            imageVector = Icons.Default.Notes,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.LightGray
                        )
                    }
                }
            }
    }
}

@Composable
private fun getCategoryIcon(category: String): ImageVector {
    return when (category) {
        "Food" -> Icons.Default.Restaurant
        "Transport" -> Icons.Default.DirectionsCar
        "Rent" -> Icons.Default.Home
        "Salary" -> Icons.Default.Payments
        "Utilities" -> Icons.Default.Lightbulb
        else -> Icons.Default.Category
    }
}