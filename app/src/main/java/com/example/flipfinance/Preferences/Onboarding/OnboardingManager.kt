package com.example.flipfinance.Preferences.Onboarding

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/*
   Title: Create an Intro/Onboarding Screen with Jetpack Compose | Kotlin | Tranquilly Coding
   Author: Tranquilly Coding
   Date: 1 years ago
   Date accessed: 25/04/2026
   Code version : 1
   Availability: https://youtu.be/AtNCGtMjavk?si=a_H9Vw6HTCQE-brD
*/

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class OnboardingManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
    }

    val hasCompletedOnboarding: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] ?: false
        }

    suspend fun saveOnboardingCompleted() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] = true
        }
    }
}