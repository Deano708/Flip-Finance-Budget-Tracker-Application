package com.example.flipfinance.ui.components.transactions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.font.FontWeight
import com.example.flipfinance.data.local.Entities.Category

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
fun CategoryDropdown(selectedCategoryId: String,
                     categoriesList: List<Category>, // Added live Database entities List
                     onCategorySelected: (Category) -> Unit,
                     onAddCategory: (String) -> Unit // Added Callback to Support on-the-fly Category Creation
     ) {

    var expanded by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    var newCategoryName by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    // Look up the Display Name for the Currently Selected ID
    val currentDisplayCategory = remember(selectedCategoryId, categoriesList) {
        categoriesList.find { it.categoryId == selectedCategoryId }?.name ?: "Select Category"
    }

    // Auto Select newly Created Category When List Updates
    LaunchedEffect(categoriesList) {
        if (newCategoryName.isNotBlank()) {
            val matchingCategory = categoriesList.find { it.name.equals(newCategoryName.trim(), ignoreCase = true) }
            if (matchingCategory != null) {
                onCategorySelected(matchingCategory)
                newCategoryName = ""    // Reset State
            }
        }
    }

    Box {
        Card(
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            shape = MaterialTheme.shapes.medium, // 16.dp from Shape.kt
            border = BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.2f)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Category", color = colorScheme.onSurface)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(currentDisplayCategory, color = colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.UnfoldMore, // You'll need Icons.Default.UnfoldMore
                        contentDescription = null,
                        tint = colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(colorScheme.surface)
        ) {
            // Show All Current Categories First
            categoriesList.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name, color = colorScheme.onSurface) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }

            // Visual Separation line for the Add Category Button
            if (categoriesList.isNotEmpty()) {
                HorizontalDivider(color = colorScheme.outlineVariant.copy(alpha = 0.3f))
            }

            // Add Category Button
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Add Custom Category", color = colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                },
                onClick = {
                    expanded = false
                    showAddDialog = true // Text Alert Overlay
                }
            )
        }

        // Inline Overlay Dialog Tracker
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("New Custom Category") },
                text = {
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        label = { Text("Category Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newCategoryName.isNotBlank()) {
                                onAddCategory(newCategoryName)
                                showAddDialog = false
                            }
                        }
                    ) { Text("Save", color = colorScheme.primary, fontWeight = FontWeight.Bold) }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) { Text("Cancel", color = colorScheme.error) }
                }
            )
        }

    }
}