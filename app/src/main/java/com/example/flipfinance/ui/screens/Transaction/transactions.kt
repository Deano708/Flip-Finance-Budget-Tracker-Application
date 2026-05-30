package com.example.flipfinance.ui.screens.Transaction

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.flipfinance.ViewModel.SettingsViewModel
import com.example.flipfinance.data.local.Entities.Transaction
import com.example.flipfinance.ViewModel.TransactionViewModel
import com.example.flipfinance.data.local.components.TransactionItem
import com.example.flipfinance.ui.components.transactions.FinanceSummaryCard
import com.example.flipfinance.ui.components.transactions.TransactionDetailsSheet
import com.example.flipfinance.ui.components.transactions.TransactionFilterRow
import com.example.flipfinance.ui.components.transactions.TransactionSearchBar
import com.example.flipfinance.ui.components.transactions.formatTransactionDate
import com.example.flipfinance.ui.screens.navigation.Screen
import kotlinx.coroutines.launch

/*
   Title: Save data in a local database using Room
   Author: Android Developers
   Date: 5 March 2026
   Date accessed: 24/04/2026
   Availability: https://developer.android.com/training/data-storage/room
*/

/*
   Title: Tutorial: The FULL Beginner Guide for Room in Android | Local Database Tutorial for Android
   Author: Philipp Lackner (YouTube)
   Date: 15 March 2023
   Date accessed: 24/04/2026
   Availability: https://www.youtube.com/watch?v=bOd3wO0uFr8
*/

/*
   Title: Accessing data using Room DAOs
   Author: Android Developers
   Date: 5 March 2026
   Date accessed: 24/04/2026
   Availability: https://developer.android.com/training/data-storage/room/accessing-data
*/

/*
   Title: Card
   Author: Android Developers
   Date: 24 April 2026
   Date accessed: 24/04/2026
   Availability: https://developer.android.com/develop/ui/compose/components/card
*/

/*
   Title: Bottom Sheets
   Author: Android Developers
   Date: 24 April 2026
   Date accessed: 26/04/2026
   Availability: https://developer.android.com/develop/ui/compose/components/bottom-sheets
*/

/*
   Title: Rows, Columns & Basic Sizing - Android Jetpack Compose - Part 2
   Author: Phillip Lackner
   Date: 5 years ago
   Date accessed: 26/04/2026
   Code version : 1
   Availability: https://youtu.be/rHKeRWK3zL4?si=BIcdBEid7DIozjYu
*/

/*
   Title: Modifiers - Android Jetpack Compose - Part 3
   Author: Phillip Lackner
   Date: 5 years ago
   Date accessed: 26/04/2026
   Code version : 1
   Availability: https://youtu.be/XCuC_p3E0qo?si=e-mzwWJ2Dx5MDG5W
*/

/*
   Title: Textfields, Buttons & Showing Snackbars - Android Jetpack Compose - Part 7
   Author: Phillip Lackner
   Date: 5 years ago
   Date accessed: 26/04/2026
   Code version : 1
   Availability: https://youtu.be/_yON9d9if6g?si=SzA1f3U4XmFhxOUw
*/

/*
   Title: State - Android Jetpack Compose - Part 6
   Author: Phillip Lackner
   Date: 5 years ago
   Date accessed: 26/04/2026
   Code version : 1
   Availability: https://youtu.be/s3m1PSd7VWc?si=W9D10o-CFGRSg9Ex
*/

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TransactionScreen(
    viewModel: TransactionViewModel,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    navController: NavController, // Added NavController for navigation
    onTransactionClick: (Transaction) -> Unit
) {
    val transactionList by viewModel.transactions.collectAsState()
    val categoryList by viewModel.categories.collectAsState()
    val settingsState by settingsViewModel.uiState.collectAsState()
    val currencySymbol = settingsState.currency.symbol
    // Filter State
    var selectedFilter by remember { mutableStateOf("All") }

    // State for Search
    var searchQuery by remember { mutableStateOf("") }

    // Dialog UI Overlay Tracker state
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    // Logic to Filter the Transactions based on Selection
    val filteredTransactions = remember(transactionList, categoryList, selectedFilter, searchQuery) {
        transactionList.filter { transaction ->
            val resolvedCategoryName = categoryList.find { it.categoryId == transaction.categoryId }?.name ?: ""

            val matchesFilter = when {
                selectedFilter.equals("All", ignoreCase = true) -> true
                selectedFilter.equals("Expense", ignoreCase = true) ||
                        selectedFilter.equals("Income", ignoreCase = true) -> {
                    transaction.expenseType.equals(selectedFilter, ignoreCase = true)
                }
                else -> resolvedCategoryName.equals(selectedFilter, ignoreCase = true)
            }

            // Check Search Query - (Title or Description)
            val matchesSearch = transaction.title.contains(searchQuery, ignoreCase = true) ||
                    transaction.description.contains(searchQuery, ignoreCase = true)

            matchesFilter && matchesSearch
        }
    }

    //Calculate the Total Amount Based on the Filtered Results
    val totalSpent = remember(filteredTransactions) {
        filteredTransactions
            .filter { it.expenseType.equals("Expense", ignoreCase = true) }
            .sumOf { it.amount }
    }

    val totalIncome = remember(filteredTransactions) {
        filteredTransactions
            .filter { it.expenseType.equals("Income", ignoreCase = true) }
            .sumOf { it.amount }
    }

    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("New Custom Category") },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Category Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            viewModel.addNewCategory(newCategoryName)
                            newCategoryName = ""
                            showAddCategoryDialog = false
                        }
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) { Text("Cancel") }
            }
        )
    }

    // for the bottom sheet
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // Transaction List
    val groupedTransactions = filteredTransactions.groupBy { formatTransactionDate(it.date) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Transactions",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = colorScheme.onBackground
                ),
                // Ensures the top bar sits flush at the very top of the Screen
                windowInsets = WindowInsets.statusBars
            )
        },
        floatingActionButton = {
            // This button navigates to the Add Transaction Bottom Sheet
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddTransaction.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            // Search Bar
            TransactionSearchBar(
                query = searchQuery,
                onQueryChanged = { searchQuery = it }
            )

            // Filter Row
            TransactionFilterRow(
                selectedFilter = selectedFilter,
                categories = categoryList,
                onFilterSelected = { selectedFilter = it },
                onAddCategoryClick = { showAddCategoryDialog = true },
                onDeleteCategoryClick = { targetCategory ->
                    viewModel.deleteCustomCategory(targetCategory)
                }
            )

            // Dynamic Category Spend
            if (filteredTransactions.isNotEmpty()) {
                FinanceSummaryCard(
                    income = totalIncome,
                    expense = totalSpent,
                    currencySymbol = currencySymbol
                )
            }

            if (filteredTransactions.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (transactionList.isEmpty()) "No transactions Yet" else "No matches Found",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    groupedTransactions.forEach { (date, transactions) ->
                        stickyHeader {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                Text(
                                    text = date,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        items(transactions) { transaction ->
                            TransactionItem(
                                transaction = transaction,
                                currencySymbol = currencySymbol,
                                categories = categoryList,
                                onClick = {
                                    selectedTransaction = transaction
                                    showSheet = true
                                }
                            )
                        }
                    }
                }
            }
        }
        // The Bottom Sheet
        if (showSheet && selectedTransaction != null) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                dragHandle = { BottomSheetDefaults.DragHandle(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                ) }
            ) {
                TransactionDetailsSheet(
                    transaction = selectedTransaction!!,
                    categories = categoryList, // <-- IMPLEMENTED STEP 4 HERE
                    currencySymbol = currencySymbol,
                    onDelete = {
                        val firebaseNodeKey = selectedTransaction!!.transactionId.toString()
                        viewModel.deleteTransaction(firebaseNodeKey)
                        showSheet = false
                    },
                    onEdit = { updatedTransaction ->
                        scope.launch {
                            try {
                                viewModel.addTransaction(updatedTransaction, null)
                            } catch (e: Exception) {
                                Log.e("EditError", "Failed to update transaction: ${e.message}")
                            }
                        }
                        showSheet = false
                    }
                )
            }
        }
    }
}