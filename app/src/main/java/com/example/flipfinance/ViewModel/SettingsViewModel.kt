package com.example.flipfinance.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flipfinance.Preferences.Settings.SettingsRepository
import com.example.flipfinance.Preferences.Settings.SettingsState
import com.example.flipfinance.Preferences.Settings.SupportedCurrency
import com.example.flipfinance.workers.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val uiState: StateFlow<SettingsState> = settingsRepository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsState())

    fun onDarkModeToggled(isDarkMode: Boolean) {
        viewModelScope.launch { settingsRepository.updateDarkMode(isDarkMode) }
    }

    // When budget alerts are toggled, also start or stop the WorkManager job
    fun onBudgetAlertsToggled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateBudgetAlerts(enabled)
            if (enabled) {
                NotificationScheduler.schedule(context)
            } else {
                NotificationScheduler.cancel(context)
            }
        }
    }

    fun onCurrencySelected(currency: SupportedCurrency) {
        viewModelScope.launch { settingsRepository.updateCurrency(currency) }
    }

    fun onMinBudgetChanged(amount: String) {
        viewModelScope.launch { settingsRepository.updateMinBudget(amount) }
    }

    fun onMaxBudgetChanged(amount: String) {
        viewModelScope.launch { settingsRepository.updateMaxBudget(amount) }
    }
}
