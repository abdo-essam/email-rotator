package com.ae.emailrotator.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.presentation.theme.StatusGreen
import com.ae.emailrotator.presentation.theme.StatusGreenBg
import com.ae.emailrotator.presentation.theme.StatusGreenDarkBg
import com.ae.emailrotator.presentation.theme.StatusRed
import com.ae.emailrotator.presentation.theme.StatusRedBg
import com.ae.emailrotator.presentation.theme.StatusRedDarkBg
import com.ae.emailrotator.presentation.theme.SurfaceDark

@Composable
fun AnimatedStatusChip(status: EmailStatus) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (status == EmailStatus.AVAILABLE) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotPulse"
    )

    val bgColor by animateColorAsState(
        targetValue = when (status) {
            EmailStatus.AVAILABLE -> StatusGreenBg
            EmailStatus.LIMITED -> StatusRedBg
        },
        label = "statusBg"
    )

    val textColor by animateColorAsState(
        targetValue = when (status) {
            EmailStatus.AVAILABLE -> StatusGreen
            EmailStatus.LIMITED -> StatusRed
        },
        label = "statusText"
    )

    val isDark = MaterialTheme.colorScheme.surface == SurfaceDark
    val actualBg = if (isDark) {
        when (status) {
            EmailStatus.AVAILABLE -> StatusGreenDarkBg
            EmailStatus.LIMITED -> StatusRedDarkBg
        }
    } else bgColor

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(actualBg)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(textColor)
        )
        Text(
            text = status.name,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}