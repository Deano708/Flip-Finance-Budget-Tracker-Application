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

