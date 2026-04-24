package com.example.flipfinance.data.local.util

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flipfinance.data.local.Entities.Transaction

@Database(entities = [Transaction::class], version = 1, exportSchema = false)
abstract class FlipFinanceDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

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