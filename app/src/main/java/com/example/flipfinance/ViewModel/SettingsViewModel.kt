package com.example.flipfinance.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flipfinance.Preferences.Settings.SettingsRepository
import com.example.flipfinance.Preferences.Settings.SettingsState
import com.example.flipfinance.Preferences.Settings.SupportedCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsState> = repository.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsState()
    )

    fun onDarkModeToggled(isDarkMode: Boolean) = viewModelScope.launch {
        repository.updateDarkMode(isDarkMode)
    }

    fun onBudgetAlertsToggled(enabled: Boolean) = viewModelScope.launch {
        repository.updateBudgetAlerts(enabled)
    }

    fun onCurrencySelected(currency: SupportedCurrency) = viewModelScope.launch {
        repository.updateCurrency(currency)
    }

    fun onMinBudgetChanged(amount: String) = viewModelScope.launch {
        // Simple validation to only allow digits/decimals could be added here
        repository.updateMinBudget(amount)
    }

    fun onMaxBudgetChanged(amount: String) = viewModelScope.launch {
        repository.updateMaxBudget(amount)
    }
}