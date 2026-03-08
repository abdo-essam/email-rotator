package com.ae.emailrotator.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlueLight,
    onPrimary = SurfaceDark,
    primaryContainer = PrimaryBlueDark,
    secondary = AccentPurple,
    tertiary = AccentTeal,
    surface = SurfaceDark,
    surfaceVariant = SurfaceCardDark,
    onSurface = OnSurfaceDark,
    onSurfaceVariant = OnSurfaceSecondaryDark,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    outline = OutlineDark,
    error = StatusRed
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = PrimaryBlueSubtle,
    secondary = AccentPurple,
    tertiary = AccentTeal,
    surface = SurfaceLight,
    surfaceVariant = SurfaceCardLight,
    onSurface = OnSurfaceLight,
    onSurfaceVariant = OnSurfaceSecondaryLight,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    outline = OutlineLight,
    error = StatusRed
)

@Composable
fun EmailRotatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = Shapes,
        content = content
    )
}