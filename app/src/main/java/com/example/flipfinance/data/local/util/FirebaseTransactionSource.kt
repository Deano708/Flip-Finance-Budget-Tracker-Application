package com.example.flipfinance.data.local.util

import android.util.Log
import com.example.flipfinance.data.local.Entities.Transaction
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FirebaseTransactionSource @Inject constructor(
    private val fbDatabase : FirebaseDatabase
) {
    // gets transactions from that user id in the database
    fun getTransactionsByUser(currentUserId: String): Flow<List<Transaction>> = callbackFlow {
        if (currentUserId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        //path in Firebase
        val ref = fbDatabase.getReference("transactions/$currentUserId")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val transactionList = mutableListOf<Transaction>()
                for (childSnapshot in snapshot.children) {
                    val tx = childSnapshot.getValue(Transaction::class.java)
                    if (tx != null) {

                        transactionList.add(tx)
                    }
                }
                // Sort by timestamp descending (equivalent to Room's ORDER BY date DESC) by the DAO
                trySend(transactionList.sortedByDescending { it.date })
            }

            override fun onCancelled(error: DatabaseError) {
                //Gives error message in logcat if the process fails to add transaction to list.
                Log.e(
                    "FirebaseTransactionSource",
                    "Failed to read transactions: ${error.message}",
                    error.toException()
                )
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun insertTransaction(currentUserId: String, transaction: Transaction, keyId: String) {
        if (currentUserId.isBlank()) return
        val ref = fbDatabase.getReference("transactions/$currentUserId").child(keyId)
        ref.setValue(transaction)
    }

    fun deleteTransaction(currentUserId: String, transactionId: String) {
        if (currentUserId.isBlank() || transactionId.isBlank()) return
        fbDatabase.getReference("transactions/$currentUserId").child(transactionId).removeValue()
    }
}

