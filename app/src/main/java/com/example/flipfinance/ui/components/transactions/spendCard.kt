package com.example.flipfinance.ui.components.transactions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/*
   Title: Rows, Columns & Basic Sizing - Android Jetpack Compose - Part 2
   Author: Phillip Lackner
   Date: 5 years ago
   Date accessed: 29/04/2026
   Code version : 1
   Availability: https://youtu.be/rHKeRWK3zL4?si=BIcdBEid7DIozjYu
*/

/*
   Title: Modifiers - Android Jetpack Compose - Part 3
   Author: Phillip Lackner
   Date: 5 years ago
   Date accessed: 29/04/2026
   Code version : 1
   Availability: https://youtu.be/XCuC_p3E0qo?si=e-mzwWJ2Dx5MDG5W
*/

/*
   Title: Textfields, Buttons & Showing Snackbars - Android Jetpack Compose - Part 7
   Author: Phillip Lackner
   Date: 5 years ago
   Date accessed: 29/04/2026
   Code version : 1
   Availability: https://youtu.be/_yON9d9if6g?si=SzA1f3U4XmFhxOUw
*/


@Composable
fun FinanceSummaryCard(
    income: Double,
    expense: Double,
    currencySymbol: String
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Income Section
            SummaryItem(
                label = "Total Income",
                amount = income,
                currencySymbol = currencySymbol,
                amountColor = colorScheme.primary, // Your Primary Green
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(colorScheme.outlineVariant.copy(alpha = 0.5f))
            )

            // Expense Section
            SummaryItem(
                label = "Total Spent",
                amount = expense,
                currencySymbol = currencySymbol,
                amountColor = colorScheme.error, // Theme Error Red
                modifier = Modifier.weight(1f),
                alignment = Alignment.End
            )
        }
    }
}

@Composable
fun SummaryItem(
    label: String,
    amount: Double,
    currencySymbol: String,
    amountColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = Alignment.Start
) {
    Column(modifier = modifier, horizontalAlignment = alignment) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "$currencySymbol ${String.format("%.2f", amount)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = amountColor
        )
    }
}