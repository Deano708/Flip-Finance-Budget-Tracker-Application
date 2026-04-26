package com.example.flipfinance.Preferences.Settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/*
   Title: Offline data storage with Jetpack DataStore
   Author: Android Developers
   Date accessed: 18/04/2026
   Availability: https://developer.android.com/topic/libraries/architecture/datastore
*/

// Extension property to create DataStore, renamed to avoid potential conflicts
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_prefs")

enum class SupportedCurrency(val symbol: String, val displayName: String) {
    ZAR("R", "South African Rand"),
    USD("$", "US Dollar"),
    EUR("€", "Euro"),
    GBP("£", "British Pound")
}

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object PreferencesKeys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val BUDGET_ALERTS = booleanPreferencesKey("budget_alerts")
        val CURRENCY = stringPreferencesKey("currency")
        val MIN_BUDGET = stringPreferencesKey("min_budget")
        val MAX_BUDGET = stringPreferencesKey("max_budget")
    }

    val settingsFlow: Flow<SettingsState> = context.settingsDataStore.data.map { preferences ->
        SettingsState(
            isDarkMode = preferences[PreferencesKeys.DARK_MODE] ?: false,
            budgetAlertsEnabled = preferences[PreferencesKeys.BUDGET_ALERTS] ?: true,
            currency = try {
                SupportedCurrency.valueOf(preferences[PreferencesKeys.CURRENCY] ?: SupportedCurrency.ZAR.name)
            } catch (e: Exception) {
                SupportedCurrency.ZAR
            },
            minBudget = preferences[PreferencesKeys.MIN_BUDGET] ?: "",
            maxBudget = preferences[PreferencesKeys.MAX_BUDGET] ?: ""
        )
    }

    suspend fun updateDarkMode(isDarkMode: Boolean) {
        context.settingsDataStore.edit { it[PreferencesKeys.DARK_MODE] = isDarkMode }
    }

    suspend fun updateBudgetAlerts(enabled: Boolean) {
        context.settingsDataStore.edit { it[PreferencesKeys.BUDGET_ALERTS] = enabled }
    }

    suspend fun updateCurrency(currency: SupportedCurrency) {
        context.settingsDataStore.edit { it[PreferencesKeys.CURRENCY] = currency.name }
    }

    suspend fun updateMinBudget(amount: String) {
        context.settingsDataStore.edit { it[PreferencesKeys.MIN_BUDGET] = amount }
    }

    suspend fun updateMaxBudget(amount: String) {
        context.settingsDataStore.edit { it[PreferencesKeys.MAX_BUDGET] = amount }
    }
}

// Data class to hold the UI state
data class SettingsState(
    val isDarkMode: Boolean = false,
    val budgetAlertsEnabled: Boolean = true,
    val currency: SupportedCurrency = SupportedCurrency.ZAR,
    val minBudget: String = "",
    val maxBudget: String = ""
)