package com.example.flipfinance.Preferences.Settings

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
import javax.inject.Inject
import javax.inject.Singleton

/*
Title: Disclosure of AI Usage in my Assessment.
• Section: SettingsManager.
• AI Tool: Gemini
• Purpose/intention : Design and syntax implementation of SettingsManager, including preference settings for dark/light theme , currecy selection and budget limits.
• Date(s) 25/04/2026.
• https://gemini.google.com/share/71c6cb29b3c5
*/

// Extension property to create DataStore
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_prefs")

enum class SupportedCurrency(val symbol: String, val displayName: String) {
    ZAR("R", "South African Rand"),
    USD("$", "US Dollar"),
    EUR("€", "Euro"),
    GBP("£", "British Pound")
}

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepository: AuthRepository // Inject AuthRepository to observe the current user
) {

    // Dynamically generate keys based on the user's UID
    private fun darkModeKey(uid: String) = booleanPreferencesKey("dark_mode_$uid")
    private fun budgetAlertsKey(uid: String) = booleanPreferencesKey("budget_alerts_$uid")
    private fun currencyKey(uid: String) = stringPreferencesKey("currency_$uid")
    private fun minBudgetKey(uid: String) = stringPreferencesKey("min_budget_$uid")
    private fun maxBudgetKey(uid: String) = stringPreferencesKey("max_budget_$uid")

    // flatMapLatest switches to a new Flow whenever the current user changes
    @OptIn(ExperimentalCoroutinesApi::class)
    val settingsFlow: Flow<SettingsState> = authRepository.currentUser.flatMapLatest { user ->
        val uid = user?.uid ?: "guest_user" // Fallback if no user is logged in

        context.settingsDataStore.data.map { preferences ->
            SettingsState(
                isDarkMode = preferences[darkModeKey(uid)] ?: false, // Default OFF
                budgetAlertsEnabled = preferences[budgetAlertsKey(uid)] ?: false, // Default OFF
                currency = try {
                    SupportedCurrency.valueOf(preferences[currencyKey(uid)] ?: SupportedCurrency.ZAR.name)
                } catch (e: Exception) {
                    SupportedCurrency.ZAR
                },
                minBudget = preferences[minBudgetKey(uid)] ?: "",
                maxBudget = preferences[maxBudgetKey(uid)] ?: ""
            )
        }
    }

    // Helper function to safely fetch the UID when updating values
    private suspend fun getCurrentUid(): String {
        return authRepository.currentUser.firstOrNull()?.uid ?: "guest_user"
    }

    suspend fun updateDarkMode(isDarkMode: Boolean) {
        val uid = getCurrentUid()
        context.settingsDataStore.edit { it[darkModeKey(uid)] = isDarkMode }
    }

    suspend fun updateBudgetAlerts(enabled: Boolean) {
        val uid = getCurrentUid()
        context.settingsDataStore.edit { it[budgetAlertsKey(uid)] = enabled }
    }

    suspend fun updateCurrency(currency: SupportedCurrency) {
        val uid = getCurrentUid()
        context.settingsDataStore.edit { it[currencyKey(uid)] = currency.name }
    }

    suspend fun updateMinBudget(amount: String) {
        val uid = getCurrentUid()
        context.settingsDataStore.edit { it[minBudgetKey(uid)] = amount }
    }

    suspend fun updateMaxBudget(amount: String) {
        val uid = getCurrentUid()
        context.settingsDataStore.edit { it[maxBudgetKey(uid)] = amount }
    }
}

// Data class to hold the UI state - notifications and dark mode default to false
data class SettingsState(
    val isDarkMode: Boolean = false,
    val budgetAlertsEnabled: Boolean = false,
    val currency: SupportedCurrency = SupportedCurrency.ZAR,
    val minBudget: String = "",
    val maxBudget: String = ""
)