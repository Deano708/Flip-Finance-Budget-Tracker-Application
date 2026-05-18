package com.example.flipfinance.ui.components.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.flipfinance.data.local.Entities.Category

/*
   Title: How To Work With Chips In Compose
   Author: Jov Milt
   Date: 5 months ago
   Date accessed: 26/04/2026
   Availability: https://youtu.be/v97B-kQU3-4?si=hlW0U5Y7LrKw78ZS
*/

@Composable
fun TransactionFilterRow(
    selectedFilter: String,
    categories: List<Category>, // Populated from our Flow data stream
    onFilterSelected: (String) -> Unit,
    onAddCategoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = listOf("All", "Expense", "Income", "Food", "Transport", "Salary", "Rent", "Other")

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Action Indicator Button
        item {
            InputChip(
                selected = false,
                onClick = onAddCategoryClick,
                label = { Text("Add") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Standard Base Filters
        val staticFilters = filters
        items(staticFilters) { filter ->
            FilterChipItem(
                label = filter,
                isSelected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) }
            )
        }

        // Dynamic Database Entities Filter List
        items(categories) { category ->
            FilterChipItem(
                label = category.name,
                // We track filtering matches via Name or unique ID
                isSelected = selectedFilter == category.name,
                onClick = { onFilterSelected(category.name) }
            )
        }
    }
}