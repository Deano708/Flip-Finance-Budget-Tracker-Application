package com.example.flipfinance.data.local.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
   Title: Tutorial: The FULL Beginner Guide for Room in Android | Local Database Tutorial for Android
   Author: Philipp Lackner (YouTube)
   Date: 15 March 2023
   Date accessed: 24/04/2026
   Availability: https://www.youtube.com/watch?v=bOd3wO0uFr8
*/

/*
   Title: Save data in a local database using Room
   Author: Android Developers
   Date: 5 March 2026
   Date accessed: 24/04/2026
   Availability: https://developer.android.com/training/data-storage/room
*/

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val transactionId: Int = 0,
    val userId: String = "", // Foreign key from Firebase Auth
    val title: String = "",
    val amount: Double = 0.0,
    val date: Long, // Storing as a timestamp (Long) is best for Room
    val categoryId: String = "", // e.g., "Food", "Transport"
    val expenseType: String = "", // e.g., "Income", "Expense"
    val description: String = "",
    val receiptUrl: String? = null // new field for reciept uploads to supabase
)

