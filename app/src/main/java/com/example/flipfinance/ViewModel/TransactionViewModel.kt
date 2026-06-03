package com.example.flipfinance.ViewModel

import android.R.attr.category
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.flipfinance.data.local.Entities.Transaction
import com.example.flipfinance.data.local.util.TransactionDao
import com.example.flipfinance.data.remote.supabase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.storage.storage
import javax.inject.Inject
import com.example.flipfinance.data.local.Entities.Category
import com.example.flipfinance.data.local.dao.CategoryDao
import com.example.flipfinance.data.local.util.FirebaseTransactionSource
import com.example.flipfinance.workers.NotificationScheduler
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

// For Home
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.UUID

/*
   Title: Save data in a local database using Room
   Author: Android Developers
   Date: 5 March 2026
   Date accessed: 24/04/2026
   Availability: https://developer.android.com/training/data-storage/room
*/

/*
   Title: Tutorial: The FULL Beginner Guide for Room in Android | Local Database Tutorial for Android
   Author: Philipp Lackner (YouTube)
   Date: 15 March 2023
   Date accessed: 24/04/2026
   Availability: https://www.youtube.com/watch?v=bOd3wO0uFr8
*/

/*
   Title: Use Supabase with Android Kotlin
   Author: Supabase
   Date: 26 April 2026
   Date accessed: 26/04/2026
   Availability: https://supabase.com/docs/guides/getting-started/quickstarts/kotlin
*/

/*
   Title: Storage | Supabase | Jetpack Compose | Tutorial | 2023
   Author: YoursSohail
   Date: 23 October 2023
   Date accessed: 26/04/2026
   Availability: https://www.youtube.com/watch?v=BqxI7ViS_-M
*/

/*
   Title:Dependency injection with Hilt
   Author: Android Developers
   Date: 22 April 2026
   Date accessed: 24/04/2026
   Availability: https://developer.android.com/training/dependency-injection/hilt-android
*/

/*
   Title: how to make use of a suspend function in kotlin
   Author: Microsoft Copilot
   Date: 30 May 2026
   Code Version: 1
   Availability: https://copilot.microsoft.com/shares/3zFig2DCQubzgp23rKDoS
*/

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val firebaseSource: FirebaseTransactionSource,
    private val categoryDao: CategoryDao,
    private val fbDatabase: FirebaseDatabase,
    private val dao: TransactionDao,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private val rtdbRef = fbDatabase.getReference("categories/$currentUserId")

    // State Holders for Filter UI selection
    val searchQuery = MutableStateFlow("")
    val selectedFilter = MutableStateFlow("All")

    // Reactive Categories Pipeline
    val categories: StateFlow<List<Category>> = categoryDao.getCategoriesByUser(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reactive Transactions Pipeline
    val transactions: StateFlow<List<Transaction>> = firebaseSource.getTransactionsByUser(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // State Holder for Selected Date range
    val selectedDateRange = MutableStateFlow<Pair<Long, Long>?>(null)

    // Unified Search and Filter Pipeline
    val filteredTransactions: StateFlow<List<Transaction>> = combine(
        transactions,
        categories,
        searchQuery,
        selectedFilter,
        selectedDateRange
    ) { txList, catList, query, filter, dateRange ->
        txList.filter { transaction ->
            val resolvedCategoryName = catList.find { it.categoryId == transaction.categoryId }?.name ?: ""

            val matchesFilter = when {
                filter.equals("All", ignoreCase = true) -> true
                filter.equals("Expense", ignoreCase = true) || filter.equals("Income", ignoreCase = true) -> {
                    transaction.expenseType.equals(filter, ignoreCase = true)
                }
                else -> resolvedCategoryName.equals(filter, ignoreCase = true)
            }

            val matchesSearch = transaction.title.contains(query, ignoreCase = true) ||
                    transaction.description.contains(query, ignoreCase = true)

            // Date Range
            val matchesDate = if (dateRange != null) {
                // Checking between start and end timestamps
                transaction.date >= dateRange.first && transaction.date <= dateRange.second
            } else {
                true
            }

            matchesFilter && matchesSearch && matchesDate
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dynamic Income/Expense Summary
    val financeSummary: StateFlow<Pair<Double, Double>> = filteredTransactions
        .map { list ->
            val income = list.filter { it.expenseType.equals("Income", ignoreCase = true) }.sumOf { it.amount }
            val expense = list.filter { it.expenseType.equals("Expense", ignoreCase = true) }.sumOf { it.amount }
            Pair(income, expense)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(0.0, 0.0))


    // Simultaneously save to RoomDB and Push up to Firebase Realtime Database
    fun addNewCategory(name: String) {
        if (name.isBlank() || currentUserId.isBlank()) return

        val uniqueId = UUID.randomUUID().toString()
        val newCategory = Category(
            categoryId = uniqueId,
            userId = currentUserId,
            name = name.trim(),
            isCustom = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            // Writes locally for RoomDB
            categoryDao.insertCategory(newCategory)

            // Write Online for firebase
            rtdbRef.child(uniqueId).setValue(newCategory)
                .addOnFailureListener {
                }
        }
    }

    // Home screen implementation - For total spent in this month
    val totalSpentThisMonth: StateFlow<Double> = transactions
        .map { list ->
            val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            list.filter {
                val cal = Calendar.getInstance().apply { timeInMillis = it.date }
                cal.get(Calendar.MONTH) == currentMonth &&
                        cal.get(Calendar.YEAR) == currentYear &&
                        it.expenseType == "Expense"
            }.sumOf { it.amount }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Home - Highest Category Spent
    val highestCategorySpend: StateFlow<Pair<String, Double>?> = combine(transactions, categories) { txList, catList ->
        txList.filter { it.expenseType == "Expense" }
            .groupBy { tx -> catList.find { it.categoryId == tx.categoryId }?.name ?: "Unknown" }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toList()
            .maxByOrNull { it.second }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val categorySpendingBreakdown: StateFlow<List<Pair<String, Double>>> = combine(transactions, categories) { txList, catList ->
        txList.filter { it.expenseType == "Expense" }
            .groupBy { tx -> catList.find { it.categoryId == tx.categoryId }?.name ?: "Unknown" }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val spendingComparison: StateFlow<Double> = transactions
        .map { list ->
            val now = Calendar.getInstance()
            val currentMonth = now.get(Calendar.MONTH)
            val lastMonth = if (currentMonth == 0) 11 else currentMonth - 1

            val thisMonthTotal = list.filter {
                val c = Calendar.getInstance().apply { timeInMillis = it.date }
                c.get(Calendar.MONTH) == currentMonth && it.expenseType == "Expense"
            }.sumOf { it.amount }

            val lastMonthTotal = list.filter {
                val c = Calendar.getInstance().apply { timeInMillis = it.date }
                c.get(Calendar.MONTH) == lastMonth && it.expenseType == "Expense"
            }.sumOf { it.amount }

            if (lastMonthTotal == 0.0) 0.0 else ((thisMonthTotal - lastMonthTotal) / lastMonthTotal) * 100
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Private helper method to resolve the true alphanumeric Firebase string key using the transactionId hash code
    private suspend fun findFirebaseKeyByHash(targetHash: Int): String? {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = fbDatabase.getReference("transactions/$currentUserId").get().await()
                for (child in snapshot.children) {
                    if ((child.key ?: "").hashCode() == targetHash) {
                        return@withContext child.key
                    }
                }
            } catch (e: Exception) {
                Log.e("KeyLookupError", "Failed resolving alphanumeric string node from hash reference match", e)
            }
            null
        }
    }

    //made use of suspend to allow for supabase to return the receipt uid so it can be stored in the Database
    suspend fun addTransaction(transaction: Transaction, imageUri: Uri?) {
        withContext(Dispatchers.IO) {
            var finalImageUrl: String? = transaction.receiptUrl

            // Find the matching transaction by its unique identifying metadata combinations
            val existingTransaction = transactions.value.find { it.transactionId == transaction.transactionId }

            // Determine the path, to get the existing target id
            val targetTxId = if (existingTransaction != null) {
                val matchingNodeKey = findFirebaseKeyByHash(transaction.transactionId)
                matchingNodeKey ?: UUID.randomUUID().toString()
            } else {
                UUID.randomUUID().toString()
            }

            if (imageUri != null) {
                try {
                    val bytes = context.contentResolver.openInputStream(imageUri).use { inputStream ->
                        inputStream?.readBytes()
                    }

                    if (bytes != null) {
                        val fileName = "receipts/${currentUserId}_${targetTxId}.jpg"
                        supabase.storage.from("RecieptStorage").upload(fileName, bytes) {
                            upsert = true
                        }
                        finalImageUrl = supabase.storage.from("RecieptStorage").publicUrl(fileName)
                    }
                } catch (e: Exception) {
                    Log.e("UploadError", "Failed to complete Supabase upload sequence: ${e.message}", e)
                    throw e
                }
            }

            val finalizedTransaction = transaction.copy(
                userId = currentUserId,
                receiptUrl = finalImageUrl
            )

            //Logging used for debugging purposes.
            try {
                val firebaseRef = fbDatabase.getReference("transactions/$currentUserId")
                firebaseRef.child(targetTxId).setValue(finalizedTransaction).await()
                Log.d("FirebaseSuccess", "Transaction successfully written/updated at node path: $targetTxId")
                NotificationScheduler.runImmediateCheck(context)
            } catch (dbEx: Exception) {
                Log.e("DatabaseError", "Firebase execution failure writing transaction: ${dbEx.message}")
                throw dbEx
            }
        }
    }

    // Delete method
    fun deleteTransactionByEntity(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            val realFirebaseKey = findFirebaseKeyByHash(transaction.transactionId)
            if (realFirebaseKey != null) {
                firebaseSource.deleteTransaction(currentUserId, realFirebaseKey)
            } else {
                Log.e("DeleteError", "Could not locate matching online node key for transaction ID: ${transaction.transactionId}")
            }
        }
    }

    fun deleteTransaction(uniqueFirebaseKeyId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseSource.deleteTransaction(currentUserId, uniqueFirebaseKeyId)
        }
    }

    fun deleteCustomCategory(category: Category) {
        if (!category.isCustom || currentUserId.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            //  Reset transactions Using this category to Prevent Broken Mappings
            dao.getTransactionsByUser(currentUserId)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000), emptyList())
                .value
                .filter { it.categoryId == category.categoryId }
                .forEach { affected ->
                    dao.insertTransaction(affected.copy(categoryId = ""))
                }

            // Remove from RoomDB (categories)
            categoryDao.deleteCategory(id = category.categoryId, userId = currentUserId)

            // Remove from Firebase RTDB
            rtdbRef.child(category.categoryId).removeValue()
                .addOnFailureListener {
                    Log.e("FirebaseDelete", "Failed to Sync Category Deletion Online")
                }
        }
    }
}