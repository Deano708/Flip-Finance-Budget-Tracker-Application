package com.example.flipfinance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.flipfinance.Preferences.Achievements.AppOpenRepository
import com.example.flipfinance.ViewModel.AuthViewModel
import com.example.flipfinance.ViewModel.MainViewModel
import com.example.flipfinance.ViewModel.SettingsViewModel
import com.example.flipfinance.ui.components.BottomBar
import com.example.flipfinance.ui.screens.Onboarding.OnboardingScreen
import com.example.flipfinance.ui.screens.navigation.NavGraph
import com.example.flipfinance.ui.screens.navigation.Screen
import com.example.flipfinance.ui.theme.FlipFinanceTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
   Title: Create an Intro/Onboarding Screen with Jetpack Compose | Kotlin | Tranquilly Coding
   Author: Tranquilly Coding
   Date: 1 years ago
   Date accessed: 25/04/2026
   Code version : 1
   Availability: https://youtu.be/AtNCGtMjavk?si=a_H9Vw6HTCQE-brD
*/



@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appOpenRepository: AppOpenRepository

    override fun onCreate(savedInstanceState: Bundle?) {

        // Install the splash screen Before Loading App
        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContent {

            val mainViewModel: MainViewModel = hiltViewModel()
            val settingsViewModel: SettingsViewModel = hiltViewModel()

            // null = Loading, false = Show Onboarding, true = To Login
            val hasCompletedOnboarding by mainViewModel.hasCompletedOnboarding.collectAsState()
            val settingsState by settingsViewModel.uiState.collectAsState()

            FlipFinanceTheme(darkTheme = settingsState.isDarkMode) {
                AnimatedContent(
                    targetState = hasCompletedOnboarding,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(500)) togetherWith
                                fadeOut(animationSpec = tween(500))
                    },
                    label = "OnboardingTransition"
                ) { completed ->
                    when (completed) {

                        null -> { /* Empty*/ }

                        false -> {
                            OnboardingScreen(onFinished = { mainViewModel.completeOnboarding() })   // Trigger Save to DataStore Preferences
                        }

                        true -> {

                            val navController = rememberNavController()
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentRoute = navBackStackEntry?.destination?.route

                            val authViewModel
                                    : AuthViewModel = hiltViewModel()
                            val currentUser by authViewModel.currentUser.collectAsState(initial = null)

                            // Only show BottomBar if not on Auth screens and User is logged in
                            val authRoutes = listOf(
                                Screen.Login.route,
                                Screen.Register.route,
                                Screen.ForgotPassword.route
                            )

                            val ShowBottomBar = currentRoute !in authRoutes && currentUser != null

                            // Handle session logout
                            LaunchedEffect(currentUser, currentRoute) {
                                if (currentUser == null && currentRoute != null && currentRoute !in authRoutes) {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                                // After login, takes you to homescreen.
                                //mark
                                else if (currentUser != null && currentRoute in authRoutes) {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                }
                            }

                            Scaffold(
                                bottomBar = {
                                    if (ShowBottomBar) {
                                        BottomBar(navController = navController)
                                    }
                                }
                            ) { innerPadding ->
                                NavGraph(
                                    navController = navController,
                                    authViewModel = authViewModel,
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Only record an open if a user is actually logged in — avoids writing
        // guest_user records on the login/onboarding screen before auth completes.
        if (::appOpenRepository.isInitialized && FirebaseAuth.getInstance().currentUser != null) {
            lifecycleScope.launch {
                appOpenRepository.recordOpenToday()
            }
        }
    }
}