package com.ae.emailrotator.presentation.theme

import android.app.Activity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Blue500,
    onPrimary = Color.White,
    primaryContainer = Blue100,
    secondary = Purple500,
    tertiary = Teal500,
    surface = Slate50,
    surfaceVariant = Color.White,
    onSurface = Slate900,
    onSurfaceVariant = Slate500,
    background = Slate50,
    outline = Slate200,
    error = Red500
)

private val DarkColors = darkColorScheme(
    primary = Blue400,
    onPrimary = Slate900,
    primaryContainer = Blue900,
    secondary = Purple500,
    tertiary = Teal500,
    surface = Slate900,
    surfaceVariant = Slate800,
    onSurface = Slate100,
    onSurfaceVariant = Slate400,
    background = Slate900,
    outline = Slate700,
    error = Red500
)

@Composable
fun EmailRotatorTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    val colors = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.surface.toArgb()
            window.navigationBarColor = colors.surface.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }
    MaterialTheme(colorScheme = colors, typography = AppTypography, content = content)
}