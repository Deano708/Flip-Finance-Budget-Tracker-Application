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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.flipfinance.ViewModel.AuthViewModel
import com.example.flipfinance.ui.components.BottomBar
import com.example.flipfinance.ui.screens.Onboarding.OnboardingScreen
import com.example.flipfinance.ui.screens.navigation.NavGraph
import com.example.flipfinance.ui.screens.navigation.Screen
import com.example.flipfinance.ui.theme.FlipFinanceTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // DEBUG TOGGLE - Set to true to force onboarding on every app launch
    private val isDevModeOnboardingActive = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlipFinanceTheme {
                // Local State to Track if Onboarding is Currently being shown - TEMRoARY - Only for Dev testing
                var showOnboarding by remember { mutableStateOf(isDevModeOnboardingActive) }

                AnimatedContent(
                    targetState = showOnboarding,
                    transitionSpec = {
                        // Fades the Onboarding out and the App in simultaneously
                        fadeIn(animationSpec = tween(500)) togetherWith
                                fadeOut(animationSpec = tween(500))
                    },
                    label = "OnboardingTransition"
                ) { targetShowOnboarding ->
                    if (targetShowOnboarding) {
                        OnboardingScreen(onFinished = { showOnboarding = false })
                    } else {
                        val navController = rememberNavController()
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route

                        val authViewModel: AuthViewModel = hiltViewModel()
                        val currentUser by authViewModel.currentUser.collectAsState(initial = null)

                        // Only show BottomBar if not on Auth screens and User is logged in
                        val authRoutes = listOf(
                            Screen.Login.route,
                            Screen.Register.route,
                            Screen.ForgotPassword.route
                        )
                        val ShowBottomBar = currentRoute !in authRoutes && currentUser != null

                        // Handle session logout
                        LaunchedEffect(currentUser) {
                            if (currentUser == null && currentRoute !in authRoutes) {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
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
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }

                }
            }
        }
    }
}