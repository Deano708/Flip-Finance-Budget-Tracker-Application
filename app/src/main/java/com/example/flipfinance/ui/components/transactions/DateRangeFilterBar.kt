package com.example.flipfinance.ui.components.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeFilterBar(
    selectedRange: Pair<Long, Long>?,
    onRangeSelected: (Pair<Long, Long>?) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    // Format Utility for Display String
    val labelText = remember(selectedRange) {
        if (selectedRange != null) {
            val formatter = SimpleDateFormat("dd MMM", Locale.getDefault())
            "${formatter.format(Date(selectedRange.first))} - ${formatter.format(Date(selectedRange.second))}"
        } else {
            "All Time"
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Main Interaction Capsule
        Button(
            onClick = { showDatePicker = true },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedRange != null) Color(0xFF4CAF50) else colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = if (selectedRange != null) Color.White else colorScheme.onSurfaceVariant
            ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.height(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = "Select Date Range",
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(text = labelText, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)

            if (selectedRange != null) {
                Spacer(Modifier.width(6.dp))
                IconButton(
                    onClick = {
                        onRangeSelected(null) // Clear Filter State
                    },
                    modifier = Modifier.size(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear Range",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }

    // Date Range Picker Overlay Sheet
    if (showDatePicker) {
        val dateRangePickerState = rememberDateRangePickerState()

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val start = dateRangePickerState.selectedStartDateMillis
                        val end = dateRangePickerState.selectedEndDateMillis
                        if (start != null && end != null) {
                            val endOfDay = end + 86399999L
                            onRangeSelected(Pair(start, endOfDay))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Apply", color = colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = colorScheme.error)
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.weight(1f).padding(top = 16.dp)
            )
        }
    }
}