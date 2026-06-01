package com.example.flipfinance.ui.screens.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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

// Handles the icon and the personalized Morning text
@Composable
fun GreetingSection(greeting: String, userName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (greeting.contains("Morning")) Icons.Default.WbSunny else Icons.Default.NightsStay,
            contentDescription = null,
            tint = SecondaryGold,
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

// Card at top with updated 4-pillar financial visualization
@Composable
fun BudgetProgressCard(
    totalSpent: Double,
    budget: Double,
    currencySymbol: String,
    minBudget: Double,
    maxBudget: Double,
    income: Double,
    expenses: Double,
    modifier: Modifier = Modifier
) {
    // 1. Math normalization setup
    // Find the absolute highest ceiling value across all 4 pillars to compute perfect relative height ratios
    val maxValue = maxOf(income, expenses, minBudget, maxBudget, 1.0)

    val incomeRatio = (income / maxValue).toFloat().coerceIn(0.10f, 1f)
    val expensesRatio = (expenses / maxValue).toFloat().coerceIn(0.10f, 1f)
    val minBudgetRatio = (minBudget / maxValue).toFloat().coerceIn(0.10f, 1f)
    val maxBudgetRatio = (maxBudget / maxValue).toFloat().coerceIn(0.10f, 1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title / Header
            Text(
                text = "Monthly Budget Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Text Overview Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Income", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(text = "$currencySymbol${String.format(Locale.ENGLISH, "%,.2f", income)}", style = MaterialTheme.typography.bodyLarge)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Expenses", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(text = "$currencySymbol${String.format(Locale.ENGLISH, "%,.2f", expenses)}", style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Graph Visualization containing all 4 Pillars side by side
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                FinancialPillColumn(
                    label = "INCOME",
                    amount = income,
                    currencySymbol = currencySymbol,
                    heightRatio = incomeRatio,
                    pillColor = Color(0xFF42E2B8) // Green
                )

                FinancialPillColumn(
                    label = "EXPENSES",
                    amount = expenses,
                    currencySymbol = currencySymbol,
                    heightRatio = expensesRatio,
                    pillColor = Color(0xFFFF8FA3) // Pink/Red
                )

                FinancialPillColumn(
                    label = "MIN BGT",
                    amount = minBudget,
                    currencySymbol = currencySymbol,
                    heightRatio = minBudgetRatio,
                    pillColor = Color(0xFF8AB4F8) // Light Blue
                )

                FinancialPillColumn(
                    label = "MAX BGT",
                    amount = maxBudget,
                    currencySymbol = currencySymbol,
                    heightRatio = maxBudgetRatio,
                    pillColor = Color(0xFF1A73E8) // Dark Blue
                )
            }
        }
    }
}

@Composable
fun RowScope.FinancialPillColumn(
    label: String,
    amount: Double,
    currencySymbol: String,
    heightRatio: Float,
    pillColor: Color
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(36.dp) // Adjusted slightly from 46.dp to fit 4 columns gracefully
                .fillMaxHeight(heightRatio * 0.75f)
                .background(
                    color = pillColor,
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
                )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "$currencySymbol${String.format(Locale.ENGLISH, "%,.0f", amount)}",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

// Reusable Insight Card
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
        modifier = modifier
            .height(130.dp)
            .clickable { onClick() },
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

// Maps Transaction entity to a row layout
@Composable
fun TransactionListItem(transaction: Transaction, currencySymbol: String, categories: List<Category>) {
    val isExpense = transaction.expenseType == "Expense"

    val categoryName = remember(transaction.categoryId, categories) {
        categories.find { it.categoryId == transaction.categoryId }?.name ?: "Other"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFFF1F3F5), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isExpense) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                contentDescription = null,
                tint = if (isExpense) Color.Red else PrimaryGreen
            )
        }

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.title,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = categoryName,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "${if (isExpense) "-" else "+"} $currencySymbol ${String.format("%.2f", transaction.amount)}",
                fontWeight = FontWeight.Bold,
                color = if (isExpense) Color(0xFFCF6679) else MaterialTheme.colorScheme.primary
            )
        }
    }
}