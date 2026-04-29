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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
fun CategoryDropdown(selectedCategory: String, onCategorySelected: (String) -> Unit) {
    val categories = listOf("Food", "Transport", "Rent", "Salary", "Utilities", "Other")
    var expanded by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

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
                    Text(selectedCategory, color = colorScheme.onSurfaceVariant,
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
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category, color = colorScheme.onSurface) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}