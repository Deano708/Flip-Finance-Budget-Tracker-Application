package com.example.flipfinance.data.local.util

import android.content.Context
import com.example.flipfinance.data.local.dao.CategoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FlipFinanceDatabase {
        return FlipFinanceDatabase.getDatabase(context)
    }

    @Provides
    fun provideTransactionDao(database: FlipFinanceDatabase): TransactionDao {
        return database.transactionDao()
    }

    // Custom Categories
    @Provides
    fun provideCategoryDao(database: FlipFinanceDatabase): CategoryDao {
        return database.categoryDao()
    }

}