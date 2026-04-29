package com.example.flipfinance.ViewModel

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
import androidx.lifecycle.viewModelScope
// For Home
import kotlinx.coroutines.flow.map
import java.util.Calendar

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



@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val dao: TransactionDao,
    @ApplicationContext private val context: Context // Hilt provides this automatically
) : ViewModel() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val transactions: StateFlow<List<Transaction>> = dao.getTransactionsByUser(currentUserId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Home screen implementation - for total spent in this month
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
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Home - Highest Category Spent
    val highestCategorySpend: StateFlow<Pair<String, Double>?> = transactions
        .map { list ->
            list.filter { it.expenseType == "Expense" }
                .groupBy { it.expenseCategory }
                .mapValues { entry -> entry.value.sumOf { it.amount } }
                .toList()
                .maxByOrNull { it.second }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Home - Data for Daily Spending Graph
    // Groups spending by day of the month for the graph UI
    val dailySpendingMap: StateFlow<Map<Int, Double>> = transactions
        .map { list ->
            val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
            list.filter {
                val cal = Calendar.getInstance().apply { timeInMillis = it.date }
                cal.get(Calendar.MONTH) == currentMonth && it.expenseType == "Expense"
            }
                .groupBy {
                    val cal = Calendar.getInstance().apply { timeInMillis = it.date }
                    cal.get(Calendar.DAY_OF_MONTH)
                }
                .mapValues { it.value.sumOf { it.amount } }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Home - Comparison logic: vs Last Month, returns the percentage increase/decrease
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

            if (lastMonthTotal == 0.0) 0.0
            else ((thisMonthTotal - lastMonthTotal) / lastMonthTotal) * 100
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)


    fun addTransaction(transaction: Transaction, imageUri: Uri?) {
        viewModelScope.launch {
            var finalImageUrl: String? = null

            imageUri?.let { uri ->
                // Use NonCancellable so the upload finishes even if the UI closes
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bytes = inputStream?.readBytes()

                    if (bytes != null) {
                        val fileName = "receipt_${System.currentTimeMillis()}.jpg"
                        val bucket = supabase.storage.from("RecieptStorage")

                        // Perform upload in a context that won't be killed mid-way
                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO + kotlinx.coroutines.NonCancellable) {
                            bucket.upload("receipts/$fileName", bytes)
                            finalImageUrl = bucket.publicUrl("receipts/$fileName")
                        }
                        Log.d("UploadSuccess", "Generated URL: $finalImageUrl")
                    }
                } catch (e: Exception) {
                    Log.e("UploadError", "Failed to upload: ${e.message}")
                }
            }

            val transactionToSave = transaction.copy(
                userId = currentUserId,
                receiptUrl = finalImageUrl
            )

            dao.insertTransaction(transactionToSave)
        }

    }

    fun deleteTransaction(id: Int) {
        viewModelScope.launch {
            dao.deleteTransaction(id)
        }
    }

}

