package com.example.fencesecurity.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = Color.White,
    primaryContainer = PrimaryColor.copy(alpha = 0.1f),
    onPrimaryContainer = PrimaryColor,
    secondary = AccentColor,
    onSecondary = Color.White,
    background = AppBackground,
    onBackground = DarkGrayText,
    surface = CardBackground,
    onSurface = DarkGrayText,
    error = Color(0xFFB00020),
    onError = Color.White
)

@Composable
fun FenceSecurityTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Forcing light theme as per request
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
