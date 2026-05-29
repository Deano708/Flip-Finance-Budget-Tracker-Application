package com.example.flipfinance.ui.screens.Transaction

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flipfinance.ViewModel.SettingsViewModel

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

/*
   Title: Accessing data using Room DAOs
   Author: Android Developers
   Date: 5 March 2026
   Date accessed: 24/04/2026
   Availability: https://developer.android.com/training/data-storage/room/accessing-data
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
   Title: Card
   Author: Android Developers
   Date: 24 April 2026
   Date accessed: 24/04/2026
   Availability: https://developer.android.com/develop/ui/compose/components/card
*/

/*
   Title: how can i make use of jetpack compose to make a dropdown of options from a database
   Author: Microsoft Copilot
   Date: 26 April 2026
   Code Version: 1
   Availability: https://copilot.microsoft.com/shares/4kNf4Zpv4nXXgkE23uoCJ
*/

@Composable
fun AddTransactionScreen(
    viewModel: TransactionViewModel,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val settingsState by settingsViewModel.uiState.collectAsState()
    val currencySymbol = settingsState.currency.symbol

    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Expense") }

    // Collect our categories flow from the database
    val categoryList by viewModel.categories.collectAsState()

    var selectedCategoryId by remember { mutableStateOf("") }
    // Auto Select the first Available category ID once the list loads
    LaunchedEffect(categoryList) {
        if (selectedCategoryId.isBlank() && categoryList.isNotEmpty()) {
            selectedCategoryId = categoryList.first().categoryId
        }
    }

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
                    Text("Cancel", color = colorScheme.error)
                }
                Text("Add Transaction", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                TextButton(onClick = {
                    if (title.isNotBlank() && amount.isNotBlank()) {
                        viewModel.addTransaction(
                            Transaction(
                                userId = "", // Handled by ViewModel
                                title = title,
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                date = System.currentTimeMillis(),
                                categoryId = selectedCategoryId,
                                expenseType = selectedType,
                                description = notes,
                                receiptUrl = null // This will be updated after upload
                            ),
                            imageUri = selectedImageUri // Pass the URI here!
                        )
                        onNavigateBack()
                    }
                }) {
                    Text("Save", fontWeight = FontWeight.Bold, color = colorScheme.primary)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Expense/Income Toggle
            TransactionTypeToggle(
                selectedType = selectedType,
                onTypeSelected = { selectedType = it }
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Details", color = colorScheme.onSurfaceVariant)

            // Details Card
            Card(
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                shape = MaterialTheme.shapes.medium, // Align with Shape.kt
                border = BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.2f)),
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
                            focusedTextColor = colorScheme.onSurface,
                            unfocusedTextColor = colorScheme.onSurface,
                            cursorColor = colorScheme.secondary
                        )
                    )

                    Divider(color = colorScheme.outlineVariant.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(currencySymbol, style = MaterialTheme.typography.headlineMedium, color = colorScheme.primary) //dynamic currency symbol
                        TextField(
                            value = amount,
                            onValueChange = { amount = it },
                            placeholder = { Text("Amount") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            textStyle = MaterialTheme.typography.headlineMedium.copy(color = colorScheme.onSurface),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = colorScheme.onSurface,
                                unfocusedTextColor = colorScheme.onSurface
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Category", style = MaterialTheme.typography.labelLarge, color = colorScheme.onSurfaceVariant)

            CategoryDropdown(
                selectedCategoryId = selectedCategoryId,
                categoriesList = categoryList,
                onCategorySelected = { clickedCategory ->
                    selectedCategoryId = clickedCategory.categoryId
                },
                onAddCategory = { freshName ->
                    viewModel.addNewCategory(freshName) // Carry Out Insertion Logic  toRoomDB and Firebase
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Receipt (Optional)", style = MaterialTheme.typography.labelLarge, color = colorScheme.onSurfaceVariant)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp) // Slightly smaller to fit with your Notes field
                    .padding(vertical = 4.dp)
                    .clickable { launcher.launch("image/*") },
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                border = BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.2f))
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    if (selectedImageUri != null) {
                        Text("Receipt Attached", color = colorScheme.primary, fontWeight = FontWeight.Bold)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Tap to add receipt", color = colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Notes", style = MaterialTheme.typography.labelLarge, color = colorScheme.onSurfaceVariant)
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorScheme.secondary,
                    unfocusedBorderColor = colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}