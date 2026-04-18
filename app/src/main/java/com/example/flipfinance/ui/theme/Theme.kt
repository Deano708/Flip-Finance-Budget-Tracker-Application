package com.example.flipfinance.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/*
   Title: THEMING AND STYLING IN JETPACK COMPOSE
   Author: Kotlin with Compose
   Date: 1 years ago
   Date accessed: 18/04/2026
   Code version : 1
   Availability: https://youtu.be/cWOUbkKVBbA?si=mgq3k0JQRheLxXO4
*/

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLight,
    secondary = SecondaryGold,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    secondary = SecondaryGold,
    background = BackgroundLight,
    surface = Color.White,
    onPrimary = Color.White
)

@Composable
fun FlipFinanceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}