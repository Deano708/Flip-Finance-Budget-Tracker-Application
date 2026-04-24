package com.example.flipfinance.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.flipfinance.data.local.Entities.Transaction
import com.example.flipfinance.data.local.util.TransactionDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val dao: TransactionDao)
    : ViewModel() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Automatically updates the UI list when the database changes
    val transactions: StateFlow<List<Transaction>> = dao.getTransactionsByUser(currentUserId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            // gets the current user from Firebase authentication
            val firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
            val uid = firebaseUser?.uid ?: ""

            if (uid.isNotEmpty()) {
                // sets the UID to the transaction
                val transactionWithId = transaction.copy(userId = uid)

                // Save the transaction to RoomDB
                dao.insertTransaction(transactionWithId)
            } else {
                // throws error if no UserID (shouldnt ever throw this)
                println("Error: No logged-in user found. Transaction not saved.")
            }
        }
    }

    fun deleteTransaction(id: Int) {
        viewModelScope.launch {
            dao.deleteTransaction(id)
        }
    }
}