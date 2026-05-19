package com.example.flipfinance.ui.screens.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.flipfinance.data.local.Entities.Category
import com.example.flipfinance.data.local.Entities.Transaction
import com.example.flipfinance.ui.theme.PrimaryGreen
import com.example.flipfinance.ui.theme.SecondaryGold
import java.util.Locale

//Title: Material Design 3 - Cards
//Author: Google
//Date: 2024
//Date accessed: 29 April 2026
//Availability: https://m3.material.io/components/cards/overview

//Title: Layouts in Jetpack Compose
//Author: Android Developers Documentation
//Date: 2024
//Date accessed: 29 April 2026
//Availability: https://developer.android.com/develop/ui/compose/layouts

//Title: Graphics in Jetpack Compose (Brushes and Gradients)
//Author: Google
//Date: 2024
//Date accessed: 29 April 2026
//Availability: https://developer.android.com/develop/ui/compose/graphics/draw/modifiers#brush

// handles the icon and the personalized Morning text
@Composable
fun GreetingSection(greeting: String, userName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Sun/Moon icon based on the greeting
        Icon(
            imageVector = if (greeting.contains("Morning")) Icons.Default.WbSunny else Icons.Default.NightsStay,
            contentDescription = null,
            tint = SecondaryGold, // Using your theme color
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = greeting,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}


// card at the top. It uses a LinearProgressIndicator
@Composable
fun BudgetProgressCard(totalSpent: Double, budget: Double, primaryColor: Color, currencySymbol: String) {
    val progress = if (budget > 0) (totalSpent / budget).toFloat().coerceIn(0f, 1f) else 0f
    val percentage = (progress * 100).toInt()

    val barColor = if (progress > 0.8f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    Card(
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth().height(190.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFB2D0F0), Color(0xFFF9D18C))
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text("Total Spent This Month", style = MaterialTheme.typography.labelMedium)
                // DYNAMIC CURRENCY APPLIED HERE
                Text(
                    text = "$currencySymbol ${String.format("%,.2f", totalSpent)}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text("$percentage% Used", style = MaterialTheme.typography.labelSmall)
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(12.dp).clip(CircleShape),
                    color = primaryColor,
                    trackColor = Color.White.copy(alpha = 0.5f)
                )
            }
        }
        Text(
            text = "$currencySymbol ${String.format("%,.2f", totalSpent)}",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

// the reusable "Insight" card for the bottom row.
@Composable
fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subValue: String? = null,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(130.dp).clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                subValue?.let { Text(it, style = MaterialTheme.typography.labelSmall, color = Color.Gray) }
            }
        }
    }
}

// maps Transaction entity from Room to a row in the "Recent Transactions" section
@Composable
fun TransactionListItem(transaction: Transaction, currencySymbol: String, categories: List<Category>) {
    val isExpense = transaction.expenseType == "Expense"

    val CategoryName = remember(transaction.categoryId, categories) {
        categories.find { it.categoryId == transaction.categoryId }?.name ?: "Other"
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(48.dp).background(Color(0xFFF1F3F5), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Icon changes based on category or type
            Icon(
                imageVector = if (isExpense) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                contentDescription = null,
                tint = if (isExpense) Color.Red else PrimaryGreen
            )
        }

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            // This will automatically be White in Dark Mode and Black in Light Mode
            Text(
                text = transaction.title,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = CategoryName,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(Modifier.weight(1f))
            Text(
                text = "${if (isExpense) "-" else "+"} $currencySymbol ${String.format("%.2f", transaction.amount)}",
                fontWeight = FontWeight.Bold,
                // Use your PrimaryGreen for income and a bright Red for expenses
                color = if (isExpense) Color(0xFFCF6679) else MaterialTheme.colorScheme.primary
            )
        }
    }
}

