package com.example.flipfinance.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

data class Badge(
    val id: String,
    val icon: ImageVector,
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
    val dateAchieved: String? = null
)