package com.example.flipfinance.data.local.util

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.flipfinance.data.local.Entities.Transaction

@Dao
interface TransactionDao {
    // Only fetch transactions belonging to the logged-in user
    @Query("SELECT * FROM transactions WHERE userId = :currentUserId ORDER BY date DESC")
    fun getTransactionsByUser(currentUserId: String): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Query("DELETE FROM transactions WHERE transactionId = :id")
    suspend fun deleteTransaction(id: Int)
}