package com.example.flipfinance.ui.screens.Transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flipfinance.data.local.Entities.Transaction
import com.example.flipfinance.ViewModel.TransactionViewModel
import com.example.flipfinance.data.local.components.TransactionItem
import com.example.flipfinance.ui.components.transactions.TransactionDetailsSheet
import com.example.flipfinance.ui.screens.navigation.Screen
import com.example.flipfinance.ViewModel.SettingsViewModel
import androidx.hilt.navigation.compose.hiltViewModel

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    viewModel: TransactionViewModel,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    navController: NavController, // Added NavController for navigation
    onTransactionClick: (Transaction) -> Unit
) {
    val transactionList by viewModel.transactions.collectAsState()
    //settings state for currency symbol
    val settingsState by settingsViewModel.uiState.collectAsState()
    val currencySymbol = settingsState.currency.symbol

    // for the bottom sheet
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("My Transactions", style = MaterialTheme.typography.titleLarge)
                }
            )
        },
        floatingActionButton = {
            // This button navigates to your new Add Transaction screen
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddTransaction.route) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { padding ->
        if (transactionList.isEmpty()) {
            // Feedback for when the database is empty
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No transactions yet. Tap '+' to start!", color = MaterialTheme.colorScheme.outline)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(transactionList) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        currencySymbol = currencySymbol, //dynamic symbol here
                        onClick = {
                            selectedTransaction = transaction
                            showSheet = true
                        }
                    )
                }

            }
            // The Bottom Sheet
            if (showSheet && selectedTransaction != null) {
                ModalBottomSheet(
                    onDismissRequest = { showSheet = false },
                    sheetState = sheetState,
                    containerColor = Color.White,
                    dragHandle = { BottomSheetDefaults.DragHandle() }
                ) {
                    TransactionDetailsSheet(
                        transaction = selectedTransaction!!,
                        onDelete = {
                            //NEED TO PASS TO TRANSACTION DELETE PAGE
                            viewModel.deleteTransaction(selectedTransaction!!.transactionId)
                            showSheet = false
                        },
                        onEdit = { updatedTransaction ->
                            viewModel.addTransaction(updatedTransaction,null) // Room uses @Insert(onConflict = REPLACE)
                            showSheet = false
                        }
                    )
                }
            }
        }
    }
}