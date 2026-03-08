package com.ae.emailrotator.presentation.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object Gradients {
    val primaryGradient = Brush.linearGradient(
        colors = listOf(PrimaryBlue, AccentPurple),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    val cardGradientLight = Brush.linearGradient(
        colors = listOf(Color.White, Color(0xFFF8FAFC)),
        start = Offset(0f, 0f),
        end = Offset(0f, Float.POSITIVE_INFINITY)
    )

    val cardGradientDark = Brush.linearGradient(
        colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A)),
        start = Offset(0f, 0f),
        end = Offset(0f, Float.POSITIVE_INFINITY)
    )

    val macGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF64748B), Color(0xFF475569))
    )

    val windowsGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF0EA5E9), Color(0xFF2563EB))
    )

    val greenGradient = Brush.linearGradient(
        colors = listOf(StatusGreen, Color(0xFF16A34A))
    )

    val redGradient = Brush.linearGradient(
        colors = listOf(StatusRed, Color(0xFFDC2626))
    )

    fun shimmer(show: Boolean) = if (show) Brush.linearGradient(
        colors = listOf(
            Color(0xFFE2E8F0),
            Color(0xFFF1F5F9),
            Color(0xFFE2E8F0)
        )
    ) else Brush.linearGradient(colors = listOf(Color.Transparent, Color.Transparent))
}