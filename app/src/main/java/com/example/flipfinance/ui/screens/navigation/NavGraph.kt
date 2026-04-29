package com.example.flipfinance.ui.screens.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.flipfinance.ViewModel.AuthViewModel
import com.example.flipfinance.ui.screens.Auth.ForgotPasswordScreen
import com.example.flipfinance.ui.screens.Auth.LoginScreen
import com.example.flipfinance.ui.screens.Auth.RegisterScreen
import com.example.flipfinance.ui.screens.Profile.ProfileScreen
import com.example.flipfinance.ViewModel.AuthEvent
import androidx.compose.animation.ExitTransition
import com.example.flipfinance.ViewModel.TransactionViewModel
import com.example.flipfinance.ui.screens.Profile.ChangeCredentialsScreen
import com.example.flipfinance.ui.screens.Settings.SettingsScreen
import com.example.flipfinance.ui.screens.Transaction.AddTransactionScreen
import com.example.flipfinance.ui.screens.Transaction.TransactionScreen
// Homescreen
import com.example.flipfinance.ui.screens.Home.HomeScreen

/*
   Title: BottomNavigation Jetpack Compose 🚀 | Android Studio | 2024
   Author: Easy Tuto
   Date: 1 years ago
   Date accessed: 18/04/2026
   Code version : 1
   Availability: https://youtu.be/O9csfKW3dZ4?si=CZON6Rp58NcyFhTy
*/

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        // Auth
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.Transactions.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Screen.Transactions.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }  // removes the current screen from the stack
            )
        }

        // Main
        composable(Screen.Home.route) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Home Screen")
            }
        }

        composable(Screen.Transactions.route) {
            // Hilt handles the factory and injection automatically here
            val transactionViewModel: TransactionViewModel = hiltViewModel()

            TransactionScreen(
                viewModel = transactionViewModel,
                navController = navController,
                onTransactionClick = { transaction ->
                    // Handle click (e.g., navigate to detail or show a toast)
                    println("Selected: ${transaction.title}")
                }
            )
        }

        composable(Screen.Home.route) {
            // Hilt handles the factory and injection automatically here
            val transactionViewModel: TransactionViewModel = hiltViewModel()

            HomeScreen(
                viewModel = hiltViewModel(),
                onNavigateToAdd = {
                    navController.navigate("add_transaction")
                },
                onNavigateToAnalytics = {
                    navController.navigate(Screen.Transactions.route)
                }
            )
        }


        composable(Screen.Streak.route) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Budget Goals")
            }
        }

        composable(Screen.Settings.route) {
            SettingsScreen()
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToChangeCredentials = { navController.navigate(Screen.ChangeCredentials.route) },
                onDeleteAccount = {
                    authViewModel.onEvent(AuthEvent.DeleteAccount)
                },
                onLogout = {
                    authViewModel.onEvent(AuthEvent.Logout)
                }
            )
        }
        composable(Screen.ChangeCredentials.route) {
            ChangeCredentialsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddTransaction.route) {
            val transactionViewModel: TransactionViewModel = hiltViewModel()
            AddTransactionScreen(
                viewModel = transactionViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

    }
}