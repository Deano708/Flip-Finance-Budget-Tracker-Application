package com.example.flipfinance.Preferences.Achievements

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.flipfinance.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/*
Title: Disclosure of AI Usage in my Assessment.
• Section: AppOpenRepository.
• AI Tool: Claude Sonnet 4.6
• Purpose/intention : Design and syntax implementation of AppOpenRepository, allowing for tracking of when app is used allowing for streaks tracking.
• Date(s) 02/06/2026.
• https://claude.ai/share/943aa681-7632-451c-84c2-b814e218caae
*/

// Separate DataStore instance from settings to keep concerns isolated
private val Context.appOpenDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_open_prefs")

@Singleton
class AppOpenRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepository: AuthRepository
) {
    // ── Key builders — scoped per user UID ───────────────────────────────────

    // Stores a comma-separated list of "YYYY-W##-DayAbbr" open records
    // e.g. "2026-W22-Mon,2026-W22-Wed,2026-W23-Fri"
    private fun openDaysKey(uid: String) = stringPreferencesKey("open_days_$uid")

    // ── Public API ────────────────────────────────────────────────────────────

    // Records today as an app-open day for the current user.
    // Deduplicates automatically — calling multiple times in one day is safe.
    suspend fun recordOpenToday() {
        val uid = getCurrentUid()
        val todayRecord = buildDayRecord()

        context.appOpenDataStore.edit { prefs ->
            val existing = prefs[openDaysKey(uid)] ?: ""
            val records = if (existing.isBlank()) mutableSetOf() else existing.split(",").toMutableSet()

            // Only add if not already recorded for today
            if (records.add(todayRecord)) {
                prefs[openDaysKey(uid)] = records.joinToString(",")
            }
        }
    }

    // Emits the current consecutive app-open streak in qualifying weeks.
    // A week qualifies when the user opens the app on 3 or more distinct days.
    // Switches to a fresh flow whenever the logged-in user changes.
    @OptIn(ExperimentalCoroutinesApi::class)
    val appOpenStreakWeeks: Flow<Int> = authRepository.currentUser.flatMapLatest { user ->
        val uid = user?.uid ?: "guest_user"

        context.appOpenDataStore.data.map { prefs ->
            val raw = prefs[openDaysKey(uid)] ?: ""
            computeStreak(raw)
        }
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    private suspend fun getCurrentUid(): String {
        return authRepository.currentUser.firstOrNull()?.uid ?: "guest_user"
    }

    // Builds a unique record string for today: "YYYY-W##-DayAbbr"
    private fun buildDayRecord(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val week = cal.get(Calendar.WEEK_OF_YEAR).toString().padStart(2, '0')
        val day = when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY    -> "Mon"
            Calendar.TUESDAY   -> "Tue"
            Calendar.WEDNESDAY -> "Wed"
            Calendar.THURSDAY  -> "Thu"
            Calendar.FRIDAY    -> "Fri"
            Calendar.SATURDAY  -> "Sat"
            else               -> "Sun"
        }
        return "$year-W$week-$day"
    }

    // Parses all stored records, groups them by week, then counts how many
    // consecutive weeks (going backwards from the most recent) had 3+ open days.
    private fun computeStreak(raw: String): Int {
        if (raw.isBlank()) return 0

        // Group distinct day records by their week bucket ("YYYY-W##")
        val weekBuckets = raw
            .split(",")
            .filter { it.isNotBlank() }
            .groupBy { record ->
                // "2026-W22-Mon" → "2026-W22"
                record.substringBeforeLast("-")
            }

        // Sort week keys descending so we walk from newest to oldest
        val sortedWeeks = weekBuckets.keys.sortedDescending()

        var streak = 0
        for (weekKey in sortedWeeks) {
            val distinctDays = weekBuckets[weekKey]?.toSet()?.size ?: 0
            if (distinctDays >= 3) {
                streak++
            } else {
                // Streak broken — stop counting
                break
            }
        }

        return streak
    }
}