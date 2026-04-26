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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    viewModel: TransactionViewModel,
    navController: NavController, // Added NavController for navigation
    onTransactionClick: (Transaction) -> Unit
) {
    val transactionList by viewModel.transactions.collectAsState()

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