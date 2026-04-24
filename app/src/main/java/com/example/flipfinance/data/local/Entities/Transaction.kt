package com.example.flipfinance.data.local.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val transactionId: Int = 0,
    val userId: String, // Foreign key from Firebase Auth
    val title: String,
    val amount: Double,
    val date: Long, // Storing as a timestamp (Long) is best for Room
    val expenseCategory: String, // e.g., "Food", "Transport"
    val expenseType: String, // e.g., "Income", "Expense"
    val description: String
)

