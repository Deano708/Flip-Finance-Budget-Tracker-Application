package com.example.flipfinance.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flipfinance.Preferences.Achievements.AppOpenRepository
import com.example.flipfinance.data.local.util.FirebaseTransactionSource
import com.example.flipfinance.domain.model.Badge
import com.example.flipfinance.domain.repository.BadgeRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import javax.inject.Inject

data class AchievementsUiState(
    val inputStreakWeeks: Int = 0,
    val appOpenStreakWeeks: Int = 0,
    val weeklyTransactionDays: Map<String, Boolean> = emptyMap(),
    val allWeeklyActivity: List<WeeklyActivity> = emptyList(),
    val badges: List<Badge> = emptyList()
)

data class WeeklyActivity(
    val weekLabel: String,
    val daysWithTransactions: Set<String>,
    val qualifies: Boolean
)

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val firebaseSource: FirebaseTransactionSource,
    private val appOpenRepository: AppOpenRepository,
    private val badgeRepository: BadgeRepository
) : ViewModel() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val uiState: StateFlow<AchievementsUiState> = combine(
        firebaseSource.getTransactionsByUser(currentUserId),
        appOpenRepository.appOpenStreakWeeks,
        badgeRepository.getBadges(currentUserId)
    ) { transactions, appOpenStreakWeeks, liveBadges ->

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
        for (week in allWeeklyActivity) {
            if (week.qualifies) inputStreakWeeks++ else break
        }

        // ── Current week day map for the compact card bubble row ──────────────
        val currentWeekDays = allWeeklyActivity.firstOrNull()?.daysWithTransactions ?: emptySet()
        val orderedDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val weeklyTransactionDays = orderedDays.associateWith { it in currentWeekDays }

        AchievementsUiState(
            inputStreakWeeks = inputStreakWeeks,
            appOpenStreakWeeks = appOpenStreakWeeks,
            weeklyTransactionDays = weeklyTransactionDays,
            allWeeklyActivity = allWeeklyActivity,
            badges = liveBadges
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AchievementsUiState())

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