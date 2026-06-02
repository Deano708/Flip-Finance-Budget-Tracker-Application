package com.example.flipfinance.ui.screens.Home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.flipfinance.ViewModel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flipfinance.ViewModel.SettingsViewModel

/*
   Title: Material Design
   Author: Google
   Date accessed: 02/06/2026
   Availability: https://m3.material.io/
*/

/*
   Title: Graphics in Compose
   Author: Android Developers
   Date accessed: 02/06/2026
   Availability: https://developer.android.com/develop/ui/compose/graphics/draw/overview
*/

/*
   Title: Advanced State and Side Effects in Jetpack Compose
   Author: Android Developers
   Date accessed: 02/06/2026
   Availability: https://developer.android.com/codelabs/jetpack-compose-advanced-state-side-effects#0
   And: https://youtu.be/TbxCz5AljQk?si=0ZDY7LOqxq2MHiI6
*/

@SuppressLint("LocalContextConfigurationRead")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryBreakdownScreen(
    viewModel: TransactionViewModel,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    // 1. Core State Hooks
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedDateRange by viewModel.selectedDateRange.collectAsState()

    // Collect dynamic user settings state
    val settingsState by settingsViewModel.uiState.collectAsState()
    val currencySymbol = settingsState.currency.symbol

    var selectedTab by remember { mutableStateOf("Expense") } // "Expense" or "Income"
    var showDatePicker by remember { mutableStateOf(false) }

    // Non-observable safe Locale extraction
    val context = LocalContext.current
    val currentLocale = remember(context) {
        context.resources.configuration.locales[0] ?: Locale.getDefault()
    }

    // 2. Reactive Data Filtering based on Selected Tab & Date Filter
    val filteredBreakdownList = remember(transactions, categories, selectedTab, selectedDateRange) {
        transactions
            .filter { tx ->
                // Filter by Expense vs Income type
                val matchesType = tx.expenseType.equals(selectedTab, ignoreCase = true)
                // Filter by Date Range if active
                val matchesDate = if (selectedDateRange != null) {
                    tx.date >= selectedDateRange!!.first && tx.date <= selectedDateRange!!.second
                } else true

                matchesType && matchesDate
            }
            .groupBy { tx -> categories.find { it.categoryId == tx.categoryId }?.name ?: "Uncategorized" }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }
    }

    val totalAmount = remember(filteredBreakdownList) { filteredBreakdownList.sumOf { it.second } }

    // Visual Chart Colour
    val chartColors = listOf(
        Color(0xFFE53935), // Crimson Red
        Color(0xFF1E88E5), // Blue
        Color(0xFF4CAF50), // Green
        Color(0xFFFFB300), // Amber Gold
        Color(0xFF8E24AA), // Purple
        Color(0xFF00ACC1), // Cyan
        Color(0xFFD81B60), // Pink
        Color(0xFFF4511E)  // Deep Orange
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spending Reports", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date Filter")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Date Filter Header Status View Bar
            DateFilterHeader(
                dateRange = selectedDateRange,
                onClearFilter = { viewModel.selectedDateRange.value = null }
            )

            // 3. Segmented Type Tab Selector
            TransactionTypeToggle(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            if (filteredBreakdownList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No $selectedTab transactions found.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Canvas Donut Graph
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(180.dp)) {
                                var startAngle = -90f
                                filteredBreakdownList.forEachIndexed { index, (_, amount) ->
                                    val sweepAngle = if (totalAmount > 0) {
                                        (amount / totalAmount).toFloat() * 360f
                                    } else 0f

                                    drawArc(
                                        color = chartColors.getOrElse(index) { Color.Gray },
                                        startAngle = startAngle,
                                        sweepAngle = sweepAngle,
                                        useCenter = false,
                                        style = Stroke(width = 28.dp.toPx())
                                    )
                                    startAngle += sweepAngle
                                }
                            }

                            // Center Label Display
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (selectedTab == "Expense") "Total Outlays" else "Total Inflow",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = String.format(currentLocale, "$currencySymbol %.2f", totalAmount),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Display List Items
                    itemsIndexed(filteredBreakdownList) { index, (categoryName, amount) ->
                        val percentage = if (totalAmount > 0) (amount / totalAmount) else 0.0
                        val color = chartColors.getOrElse(index) { Color.Gray }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(categoryName, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        text = String.format(currentLocale, "$currencySymbol %.2f", amount),
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedTab == "Expense") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = String.format(currentLocale, "%.1f%% of total", percentage * 100),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }

    if (showDatePicker) {
        DateRangePickerDialog(
            onDismiss = { showDatePicker = false },
            onDatesSelected = { start: Long, end: Long ->
                viewModel.selectedDateRange.value = Pair(start, end)
                showDatePicker = false
            }
        )
    }
}

// Custom UI Segmented Component
@Composable
fun TransactionTypeToggle(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(48.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
            .padding(4.dp)
    ) {
        listOf("Expense", "Income").forEach { tab ->
            val isSelected = selectedTab == tab
            val bgButtonColor = if (isSelected) MaterialTheme.colorScheme.error else Color.Transparent
            val contentColor = if (isSelected) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onSurfaceVariant

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(20.dp))
                    .background(bgButtonColor)
                    .clickable { onTabSelected(tab) },
                contentAlignment = Alignment.Center
            ) {
                Text(text = tab, fontWeight = FontWeight.Bold, color = contentColor, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun DateFilterHeader(
    dateRange: Pair<Long, Long>?,
    onClearFilter: () -> Unit
) {
    AnimatedVisibility(visible = dateRange != null) {
        dateRange?.let {
            val sdf = SimpleDateFormat("dd MMM yyyy", LocalLocale.current.platformLocale)
            val startStr = sdf.format(Date(it.first))
            val endStr = sdf.format(Date(it.second))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Period: $startStr - $endStr", color = MaterialTheme.colorScheme.onSecondaryContainer, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Clear",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.clickable { onClearFilter() }.padding(4.dp),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerDialog(
    onDismiss: () -> Unit,
    onDatesSelected: (Long, Long) -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val start = dateRangePickerState.selectedStartDateMillis
                    val end = dateRangePickerState.selectedEndDateMillis
                    if (start != null && end != null) {
                        onDatesSelected(start, end)
                    }
                },
                enabled = dateRangePickerState.selectedStartDateMillis != null && dateRangePickerState.selectedEndDateMillis != null
            ) {
                Text("Apply")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        text = {
            Column(modifier = Modifier.size(width = 400.dp, height = 500.dp)) {
                DateRangePicker(
                    state = dateRangePickerState,
                    modifier = Modifier.weight(1f),
                    title = { Text("Select Statement Window", modifier = Modifier.padding(16.dp)) },
                    headline = { Text("Filter Transactions", modifier = Modifier.padding(horizontal = 16.dp)) }
                )
            }
        }
    )
}