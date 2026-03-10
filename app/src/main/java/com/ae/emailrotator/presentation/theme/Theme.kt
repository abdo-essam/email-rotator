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
    primary = Blue600,
    onPrimary = Color.White,
    primaryContainer = Blue50,
    onPrimaryContainer = Blue600,
    secondary = Purple500,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    tertiary = Teal500,
    surface = Color.White,
    surfaceVariant = Slate100,
    onSurface = Slate900,
    onSurfaceVariant = Slate500,
    background = Slate50,
    onBackground = Slate900,
    outline = Slate200,
    outlineVariant = Slate100,
    error = Red500,
    onError = Color.White,
    errorContainer = Red50,
    onErrorContainer = Red600
)

private val DarkColors = darkColorScheme(
    primary = Blue400,
    onPrimary = Slate950,
    primaryContainer = Blue900,
    onPrimaryContainer = Blue100,
    secondary = Purple400,
    onSecondary = Slate950,
    secondaryContainer = Color(0xFF3B0764),
    tertiary = Teal400,
    surface = Slate900,
    surfaceVariant = Slate800,
    onSurface = Slate100,
    onSurfaceVariant = Slate400,
    background = Slate950,
    onBackground = Slate100,
    outline = Slate700,
    outlineVariant = Slate800,
    error = Red400,
    onError = Slate950,
    errorContainer = Red900,
    onErrorContainer = Red100
)

@Composable
fun EmailRotatorTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
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
    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}