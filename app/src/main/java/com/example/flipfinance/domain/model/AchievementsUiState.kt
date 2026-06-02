package com.example.flipfinance.domain.model

import com.example.flipfinance.ViewModel.WeeklyActivity

data class AchievementsUiState(
    val inputStreakWeeks: Int = 0,
    val appOpenStreakWeeks: Int = 0,
    val weeklyTransactionDays: Map<String, Boolean> = emptyMap(),
    val allWeeklyActivity: List<WeeklyActivity> = emptyList(),
    val badges: List<Badge> = emptyList(),
    val currentRank: Int = 0,
    val totalParticipants: Int = 0,
    val fullLeaderboard: List<LeaderboardUser> = emptyList()
)