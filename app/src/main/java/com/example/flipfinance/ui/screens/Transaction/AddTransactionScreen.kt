package com.example.flipfinance.ui.screens.Transaction

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.flipfinance.ViewModel.TransactionViewModel
import com.example.flipfinance.data.local.Entities.Transaction
import com.example.flipfinance.ui.components.transactions.CategoryDropdown
import com.example.flipfinance.ui.components.transactions.TransactionTypeToggle


@Composable
fun AddTransactionScreen(
    viewModel: TransactionViewModel,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Expense") }
    var selectedCategory by remember { mutableStateOf("Food") }
    var notes by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onNavigateBack) {
                    Text("Cancel", color = Color.Red)
                }
                Text("Add Transaction", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                TextButton(onClick = {
                    if (title.isNotBlank() && amount.isNotBlank()) {
                        viewModel.addTransaction(
                            Transaction(
                                userId = "", // Handled by ViewModel (Firbase auth)
                                title = title,
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                date = System.currentTimeMillis(),
                                expenseCategory = selectedCategory,
                                expenseType = selectedType,
                                description = notes
                            )
                        )
                        onNavigateBack()
                    }
                }) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Expense/Income Toggle
            TransactionTypeToggle(
                selectedType = selectedType,
                onTypeSelected = { selectedType = it }
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Details", color = Color.Gray)

            // Details Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("Title") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("R", style = MaterialTheme.typography.titleLarge)
                        TextField(
                            value = amount,
                            onValueChange = { amount = it },
                            placeholder = { Text("Amount") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Category", color = Color.Gray)
            CategoryDropdown(selectedCategory) { selectedCategory = it }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Receipt (Optional)", color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp) // Slightly smaller to fit with your Notes field
                    .padding(vertical = 4.dp)
                    .clickable { launcher.launch("image/*") },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    if (selectedImageUri != null) {
                        Text("Receipt Attached ✅", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Note: You may need to import Icons.Default.AddAPhoto
                            Text("Tap to add receipt", color = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Notes", color = Color.Gray)
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}