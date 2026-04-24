package com.example.flipfinance.data.local.util

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
}