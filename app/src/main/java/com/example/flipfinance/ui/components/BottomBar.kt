package com.example.flipfinance.ui.components

import android.net.http.SslCertificate.restoreState
import android.net.http.SslCertificate.saveState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.flipfinance.ui.screens.navigation.Screen

/*
   Title: BottomNavigation Jetpack Compose 🚀 | Android Studio | 2024
   Author: Easy Tuto
   Date: 1 years ago
   Date accessed: 18/04/2026
   Code version : 1
   Availability: https://youtu.be/O9csfKW3dZ4?si=CZON6Rp58NcyFhTy
*/


@Composable
fun BottomBar(navController: NavController) {
    val items = listOf(Screen.Home, Screen.Transactions, Screen.Streak, Screen.Settings, Screen.Profile)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        items.forEach { screen ->
            val isSelected = currentRoute == screen.route

            NavigationBarItem(
                icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                label = { Text(screen.title!!) },
                selected = isSelected,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ),
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}