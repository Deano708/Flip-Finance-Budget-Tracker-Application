package com.example.flipfinance.data.local.util

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flipfinance.data.local.Entities.Category
import com.example.flipfinance.data.local.Entities.Transaction
import com.example.flipfinance.data.local.dao.CategoryDao

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

// instantiates the database so it can be used througout the app.
@Database(entities = [Transaction::class, Category::class], version = 2, exportSchema = false)
abstract class FlipFinanceDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: FlipFinanceDatabase? = null

        fun getDatabase(context: Context): FlipFinanceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlipFinanceDatabase::class.java,
                    "flip_finance_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}