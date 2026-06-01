package com.example.flipfinance.ui.home

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

fun getGreeting(): String {
    val hour = java.util.Calendar.getInstance()

        .get(java.util.Calendar.HOUR_OF_DAY)

    return when (hour) {
        in 0..11 -> "Good Morning"

        in 12..17 -> "Good Afternoon"

        else -> "Good Evening"
    }

}
