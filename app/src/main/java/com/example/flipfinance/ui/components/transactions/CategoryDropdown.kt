package com.example.flipfinance.ui.components.transactions

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


@Composable
fun CategoryDropdown(selectedCategory: String, onCategorySelected: (String) -> Unit) {
    val categories = listOf("Food", "Transport", "Rent", "Salary", "Utilities", "Other")
    var expanded by remember { mutableStateOf(false) }

    Box {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Category", color = Color.Black)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(selectedCategory, color = Color.LightGray)
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.UnfoldMore, // You'll need Icons.Default.UnfoldMore
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}