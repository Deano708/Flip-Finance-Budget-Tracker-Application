package com.example.flipfinance.ui.screens.Onboarding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Science
import androidx.compose.ui.graphics.vector.ImageVector

data class Onboarding(
    val title: String,
    val description: String,
    val icon: ImageVector
)

val pages = listOf(
    Onboarding(
        "Smart Tracking",
        "Log and monitor your expenses with simple, Precise Tools",
        Icons.Default.Science
    ),
    Onboarding(
        "Live Insights",
        "See your Spending Patterns update instantly, at Anytime",
        Icons.Default.QueryStats
    ),
    Onboarding(
        "Achieve Goals",
        "Set financial targets and stay motivated every Step of the Way",
        Icons.Default.MilitaryTech
    )
)