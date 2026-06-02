package com.example.flipfinance.domain.model

data class LeaderboardUser(
    val uid: String = "",
    val displayName: String = "",
    val streakWeeks: Int = 0,
    val lastUpdated: Long = 0L
)