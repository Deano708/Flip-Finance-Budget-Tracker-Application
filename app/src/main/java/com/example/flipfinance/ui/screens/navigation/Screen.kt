package com.example.flipfinance.ui.screens.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String? = null, val icon: ImageVector? = null) {

    // Auth Routes
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")

    // Main App Routes
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Transactions : Screen("transactions", "Transact", Icons.Default.CreditCard)
    object Streak : Screen("streak", "Streak", Icons.Default.Crop)
    object Settings : Screen("settings", "Settings", Icons.Default.Crop)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
}