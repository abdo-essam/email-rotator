package com.ae.emailrotator.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ae.emailrotator.R
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.presentation.theme.*

// ─── Status Dot ────────────────────────────────────────────────────────────

@Composable
fun StatusDot(status: EmailStatus, size: Dp = 8.dp) {
    val color by animateColorAsState(
        targetValue = statusColor(status),
        label = "dotColor"
    )
    val pulse = rememberInfiniteTransition(label = "pulse")
    val scale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = if (status == EmailStatus.AVAILABLE) 1.4f else 1f,
        animationSpec = infiniteRepeatable(
            tween(1000, easing = EaseInOutSine),
            RepeatMode.Reverse
        ),
        label = "dotScale"
    )
    Box(
        Modifier
            .size(size)
            .scale(scale)
            .clip(CircleShape)
            .background(color)
    )
}

// ─── Status Chip ───────────────────────────────────────────────────────────

@Composable
fun StatusChip(status: EmailStatus) {
    val isDark = MaterialTheme.colorScheme.background == Slate950

    val (bgColor, fgColor, label) = when (status) {
        EmailStatus.AVAILABLE -> Triple(
            if (isDark) Green500.copy(alpha = 0.18f) else Green50,
            if (isDark) Green400 else Green600,
            stringResource(R.string.status_available)
        )
        EmailStatus.LIMITED -> Triple(
            if (isDark) Red500.copy(alpha = 0.18f) else Red50,
            if (isDark) Red400 else Red600,
            stringResource(R.string.status_limited)
        )
        EmailStatus.NEEDS_VERIFICATION -> Triple(
            if (isDark) Amber500.copy(alpha = 0.18f) else Amber50,
            if (isDark) Amber400 else Amber600,
            stringResource(R.string.status_needs_verification)
        )
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(
                width = 1.dp,
                color = fgColor.copy(alpha = 0.25f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        StatusDot(status, size = 7.dp)
        Text(
            text = label,
            color = fgColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ─── Glass Card ────────────────────────────────────────────────────────────

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background == Slate950
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .shadow(
                elevation = if (isDark) 0.dp else 2.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.05f)
            )
            .clip(shape)
            .background(
                color = if (isDark) Slate800 else Color.White
            )
            .border(
                width = 1.dp,
                color = if (isDark) Slate700 else Slate200,
                shape = shape
            )
            .padding(16.dp),
        content = content
    )
}

// ─── Gradient Box ──────────────────────────────────────────────────────────

@Composable
fun GradientBox(
    gradient: Brush,
    size: Dp = 44.dp,
    cornerRadius: Dp = 12.dp,
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

// ─── Stat Card ─────────────────────────────────────────────────────────────

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    tintColor: Color,
    onClick: (() -> Unit)? = null
) {
    val isDark = MaterialTheme.colorScheme.background == Slate950
    val shape = RoundedCornerShape(16.dp)
    val bgColor = if (isDark) Slate800 else Color.White
    val borderColor = if (isDark) Slate700 else Slate200

    val cardModifier = modifier
        .shadow(
            elevation = if (isDark) 0.dp else 2.dp,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = 0.05f)
        )
        .clip(shape)
        .background(bgColor)
        .border(1.dp, borderColor, shape)
        .then(
            if (onClick != null) Modifier.clickable(onClick = onClick)
            else Modifier
        )
        .padding(16.dp)

    Column(modifier = cardModifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tintColor.copy(alpha = if (isDark) 0.18f else 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tintColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ─── Tool Usage Row Card ────────────────────────────────────────────────────

@Composable
fun ToolUsageCard(
    toolName: String,
    totalEmails: Int,
    activeEmails: Int,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == Slate950
    val shape = RoundedCornerShape(14.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isDark) 0.dp else 1.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.04f)
            )
            .clip(shape)
            .background(if (isDark) Slate800 else Color.White)
            .border(1.dp, if (isDark) Slate700 else Slate200, shape)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Blue500.copy(alpha = if (isDark) 0.18f else 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                tint = Blue500,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = toolName,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PillBadge(
                text = "$totalEmails total",
                bgColor = if (isDark) Slate700 else Slate100,
                textColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
            PillBadge(
                text = "$activeEmails active",
                bgColor = if (isDark) Green500.copy(alpha = 0.18f) else Green50,
                textColor = if (isDark) Green400 else Green600
            )
        }
    }
}

// ─── Pill Badge ────────────────────────────────────────────────────────────

@Composable
fun PillBadge(
    text: String,
    bgColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ─── Helpers ───────────────────────────────────────────────────────────────

fun statusColor(status: EmailStatus): Color = when (status) {
    EmailStatus.AVAILABLE -> Green500
    EmailStatus.LIMITED -> Red500
    EmailStatus.NEEDS_VERIFICATION -> Amber500
}
