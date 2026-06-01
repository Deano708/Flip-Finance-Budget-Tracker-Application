package com.example.flipfinance.data.local.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "categories")
data class Category(
    // String UUIDs to Ensure Identical Matching keys between Room and Firebase
    @PrimaryKey
    val categoryId: String = "",
    val userId: String = "",
    val name: String = "",
    val iconName: String = "Category", // Store reference names for Vector icons
    val isCustom: Boolean = false       // True if user-created - False for system defaults
)