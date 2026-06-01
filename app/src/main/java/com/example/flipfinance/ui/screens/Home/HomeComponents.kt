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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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

                if (minBudget > 0.0) {
                    FinancialPillColumn(
                        label = "MIN BGT",
                        amount = minBudget,
                        currencySymbol = currencySymbol,
                        heightRatio = minBudgetRatio,
                        pillColor = Color(0xFF8AB4F8) // Light blue
                    )
                }

                if (maxBudget > 0.0) {
                    FinancialPillColumn(
                        label = "MAX BGT",
                        amount = maxBudget,
                        currencySymbol = currencySymbol,
                        heightRatio = maxBudgetRatio,
                        pillColor = Color(0xFF1A73E8) // Dark blue
                    )
                }
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

// 1. Updated Interactive Summary Card Component (Placed in bottom-left pill box)
@Composable
fun DynamicBudgetCard(
    modifier: Modifier = Modifier,
    currencySymbol: String,
    currentExpenses: Double,
    maxBudget: Double,
    minBudget: Double,
    icon: ImageVector,
    iconColor: Color,
    onBudgetsSaved: (Double, Double) -> Unit // Dispatches new amounts to your ViewModel pipeline
) {
    var showDialog by remember { mutableStateOf(false) }

    // Math metrics logic
    val budgetStatusString = remember(currentExpenses, maxBudget) {
        if (maxBudget <= 0.0) {
            "Tap to configure limits"
        } else if (currentExpenses >= maxBudget) {
            "Over limit ceiling!"
        } else {
            val leftover = maxBudget - currentExpenses
            "$currencySymbol${String.format(Locale.ENGLISH, "%,.0f", leftover)} remaining"
        }
    }

    val displayPercentage = remember(currentExpenses, maxBudget) {
        if (maxBudget > 0.0) {
            val percentage = (currentExpenses / maxBudget) * 100
            String.format(Locale.ENGLISH, "%.1f%% configured", percentage)
        } else {
            "+0.00% variance"
        }
    }

    SummaryCard(
        modifier = modifier,
        title = "Budget Target Tracker",
        value = displayPercentage,
        subValue = budgetStatusString,
        icon = icon,
        iconColor = iconColor,
        onClick = { showDialog = true }
    )

    if (showDialog) {
        BudgetConfigurationDialog(
            initialMinBudget = if (minBudget > 0.0) minBudget.toString() else "",
            initialMaxBudget = if (maxBudget > 0.0) maxBudget.toString() else "",
            onDismiss = { showDialog = false },
            onSave = { updatedMin, updatedMax ->
                onBudgetsSaved(updatedMin, updatedMax)
                showDialog = false
            }
        )
    }
}

// 2. Composable Pop-up Input Dialog View
@Composable
fun BudgetConfigurationDialog(
    initialMinBudget: String,
    initialMaxBudget: String,
    onDismiss: () -> Unit,
    onSave: (Double, Double) -> Unit
) {
    var minInput by remember { mutableStateOf(initialMinBudget) }
    var maxInput by remember { mutableStateOf(initialMaxBudget) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Configure Budget Limits",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Specify your lower limit safety targets and strict ceiling limitations below.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                OutlinedTextField(
                    value = minInput,
                    onValueChange = { input -> if (input.all { it.isDigit() || it == '.' }) minInput = input },
                    label = { Text("Minimum Budget Threshold") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = maxInput,
                    onValueChange = { input -> if (input.all { it.isDigit() || it == '.' }) maxInput = input },
                    label = { Text("Maximum Budget Ceiling") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalMin = minInput.toDoubleOrNull() ?: 0.0
                    val finalMax = maxInput.toDoubleOrNull() ?: 0.0
                    onSave(finalMin, finalMax)
                }
            ) {
                Text("Apply Parameters")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}