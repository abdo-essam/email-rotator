package com.ae.emailrotator.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.presentation.theme.*

@Composable
fun StatusDot(status: EmailStatus) {
    val color by animateColorAsState(
        targetValue = when (status) {
            EmailStatus.AVAILABLE -> Green500
            EmailStatus.LIMITED -> Red500
        }, label = "dotColor"
    )
    val pulse = rememberInfiniteTransition(label = "pulse")
    val scale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = if (status == EmailStatus.AVAILABLE) 1.3f else 1f,
        animationSpec = infiniteRepeatable(
            tween(1200, easing = EaseInOutCubic), RepeatMode.Reverse
        ), label = "dotScale"
    )
    Box(
        Modifier
            .size(10.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun StatusChip(status: EmailStatus) {
    val isDark = MaterialTheme.colorScheme.surface == Slate900
    val (bg, fg) = when (status) {
        EmailStatus.AVAILABLE -> if (isDark) Pair(Green900, Green500) else Pair(Green100, Green500)
        EmailStatus.LIMITED -> if (isDark) Pair(Red900, Red500) else Pair(Red100, Red500)
    }
    Box(
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            StatusDot(status)
            Text(
                status.name,
                color = fg,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = MaterialTheme.colorScheme.surface == Slate900
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = modifier
            .shadow(6.dp, shape, ambientColor = Color.Black.copy(alpha = 0.06f))
            .clip(shape)
            .background(
                if (isDark) Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.07f), Color.White.copy(alpha = 0.03f))
                ) else Brush.verticalGradient(
                    listOf(Color.White, Color.White.copy(alpha = 0.9f))
                )
            )
            .border(
                1.dp,
                if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.5f),
                shape
            )
            .padding(16.dp),
        content = content
    )
}

@Composable
fun GradientBox(
    gradient: Brush,
    size: Dp = 44.dp,
    cornerRadius: Dp = 14.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        Modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .background(gradient),
        contentAlignment = Alignment.Center,
        content = content
    )
}
