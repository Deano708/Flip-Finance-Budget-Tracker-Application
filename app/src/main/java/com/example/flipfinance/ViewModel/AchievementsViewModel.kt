package com.example.flipfinance.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flipfinance.Preferences.Achievements.AppOpenRepository
import com.example.flipfinance.data.local.util.FirebaseTransactionSource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import javax.inject.Inject

/*
   Title: Save data in a local database using Room
   Author: Android Developers
   Date: 5 March 2026
   Date accessed: 24/04/2026
   Availability: https://developer.android.com/training/data-storage/room
*/

/*
   Title: Dependency injection with Hilt
   Author: Android Developers
   Date: 22 April 2026
   Date accessed: 24/04/2026
   Availability: https://developer.android.com/training/dependency-injection/hilt-android
*/

// Represents the aggregated achievements state for the UI
data class AchievementsUiState(
    val inputStreakWeeks: Int = 0,
    val appOpenStreakWeeks: Int = 0,
    val weeklyTransactionDays: Map<String, Boolean> = emptyMap(),
    val allWeeklyActivity: List<WeeklyActivity> = emptyList(),
    val badges: List<Badge> = emptyList()
)

// Represents a single week's transaction day activity for the detail table
data class WeeklyActivity(
    val weekLabel: String,
    val daysWithTransactions: Set<String>,
    val qualifies: Boolean
)

// Placeholder badge data class for future implementation
data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val isEarned: Boolean = false
)

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val firebaseSource: FirebaseTransactionSource,
    private val appOpenRepository: AppOpenRepository          // Injected — no longer a placeholder
) : ViewModel() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val badgeCatalogue = listOf(
        Badge("first_tx",    "First Step",      "Log your very first transaction",           "Payments",            false),
        Badge("ten_tx",      "Getting Started", "Record 10 transactions in total",           "TrendingUp",          false),
        Badge("fifty_tx",    "Committed",       "Record 50 transactions in total",           "Star",                false),
        Badge("week_streak", "Consistent",      "Maintain a 4-week input streak",            "LocalFireDepartment", false),
        Badge("no_expense",  "Saver",           "Have a week with no expenses logged",       "Savings",             false),
        Badge("big_income",  "Payday",          "Log an income transaction over R10 000",    "AttachMoney",         false),
        Badge("multi_cat",   "Diversified",     "Use 5 different categories in one month",  "Category",            false),
        Badge("receipt",     "Paper Trail",     "Attach a receipt to any transaction",       "Receipt",             false)
    )

    // Combine both flows so the UI reacts to changes in either transactions or app-open data
    val uiState: StateFlow<AchievementsUiState> = combine(
        firebaseSource.getTransactionsByUser(currentUserId),
        appOpenRepository.appOpenStreakWeeks
    ) { transactions, appOpenStreakWeeks ->

        // ── Group all transactions by ISO week bucket (year-week) ─────────────
        val weekBuckets = mutableMapOf<String, MutableSet<String>>()

        transactions.forEach { tx ->
            val cal = Calendar.getInstance().apply { timeInMillis = tx.date }
            val year = cal.get(Calendar.YEAR)
            val week = cal.get(Calendar.WEEK_OF_YEAR)
            val weekKey = "$year-W${week.toString().padStart(2, '0')}"

            val dayLabel = when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY    -> "Mon"
                Calendar.TUESDAY   -> "Tue"
                Calendar.WEDNESDAY -> "Wed"
                Calendar.THURSDAY  -> "Thu"
                Calendar.FRIDAY    -> "Fri"
                Calendar.SATURDAY  -> "Sat"
                else               -> "Sun"
            }
            weekBuckets.getOrPut(weekKey) { mutableSetOf() }.add(dayLabel)
        }

        // ── Build sorted weekly activity list ─────────────────────────────────
        val allWeeklyActivity = weekBuckets.entries
            .sortedByDescending { it.key }
            .map { (weekKey, days) ->
                WeeklyActivity(
                    weekLabel = buildWeekLabel(weekKey),
                    daysWithTransactions = days,
                    qualifies = days.size >= 3
                )
            }

        // ── Input streak: consecutive qualifying weeks going backwards ─────────
        var inputStreakWeeks = 0
        for (week in allWeeklyActivity) {       // already sorted newest-first
            if (week.qualifies) inputStreakWeeks++ else break
        }

        // ── Current week day map for the compact card bubble row ──────────────
        val currentWeekDays = allWeeklyActivity.firstOrNull()?.daysWithTransactions ?: emptySet()
        val orderedDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val weeklyTransactionDays = orderedDays.associateWith { it in currentWeekDays }

        AchievementsUiState(
            inputStreakWeeks = inputStreakWeeks,
            appOpenStreakWeeks = appOpenStreakWeeks,     // Now live from DataStore
            weeklyTransactionDays = weeklyTransactionDays,
            allWeeklyActivity = allWeeklyActivity,
            badges = badgeCatalogue
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AchievementsUiState())

    // Converts "2026-W22" into a human-readable label like "25 May – 31 May"
    private fun buildWeekLabel(weekKey: String): String {
        return try {
            val parts = weekKey.split("-W")
            val year = parts[0].toInt()
            val week = parts[1].toInt()

            val cal = Calendar.getInstance().apply {
                clear()
                set(Calendar.YEAR, year)
                set(Calendar.WEEK_OF_YEAR, week)
                set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            }
            val startDay = cal.get(Calendar.DAY_OF_MONTH)
            val startMonth = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, java.util.Locale.getDefault()) ?: ""
            cal.add(Calendar.DAY_OF_WEEK, 6)
            val endDay = cal.get(Calendar.DAY_OF_MONTH)
            val endMonth = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, java.util.Locale.getDefault()) ?: ""

            "$startDay $startMonth – $endDay $endMonth"
        } catch (e: Exception) {
            weekKey
        }
    }
}