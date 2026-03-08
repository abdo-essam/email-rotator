package com.ae.emailrotator.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ae.emailrotator.domain.model.DeviceType
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.presentation.theme.AccentPurple
import com.ae.emailrotator.presentation.theme.Gradients
import com.ae.emailrotator.presentation.theme.PrimaryBlue
import com.ae.emailrotator.presentation.theme.PrimaryBlueSubtle
import com.ae.emailrotator.presentation.theme.StatusAmber
import com.ae.emailrotator.presentation.theme.StatusAmberBg
import com.ae.emailrotator.presentation.theme.StatusGreen
import com.ae.emailrotator.presentation.theme.StatusGreenBg
import com.ae.emailrotator.presentation.theme.StatusRed
import com.ae.emailrotator.presentation.theme.StatusRedBg
import com.ae.emailrotator.util.DateTimeUtil

@Composable
fun DeviceCard(
    deviceName: String,
    deviceType: DeviceType,
    toolCount: Int,
    totalEmails: Int,
    availableEmails: Int,
    limitedEmails: Int,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradient = when (deviceType) {
        DeviceType.MAC -> Gradients.macGradient
        DeviceType.WINDOWS -> Gradients.windowsGradient
    }

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GradientIconBox(gradient = gradient, size = 52) {
                Text(
                    text = deviceType.icon,
                    fontSize = 24.sp
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = deviceName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = deviceType.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onEditClick) {
                Icon(
                    Icons.Outlined.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = StatusRed.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Stats row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(value = toolCount.toString(), label = "Tools", color = PrimaryBlue)
            StatDivider()
            StatItem(value = totalEmails.toString(), label = "Emails", color = AccentPurple)
            StatDivider()
            StatItem(value = availableEmails.toString(), label = "Active", color = StatusGreen)
            StatDivider()
            StatItem(value = limitedEmails.toString(), label = "Limited", color = StatusRed)
        }

        Spacer(Modifier.height(12.dp))

        FilledTonalButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Visibility, contentDescription = null, Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("View Details")
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(36.dp)
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    )
}

@Composable
fun ModernToolCard(
    toolName: String,
    activeEmail: String?,
    emailCount: Int,
    availableCount: Int,
    limitedCount: Int,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GradientIconBox(
                gradient = Brush.linearGradient(listOf(AccentPurple, PrimaryBlue)),
                size = 44
            ) {
                Icon(
                    Icons.Default.Extension,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = toolName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                if (activeEmail != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PulsingDot(color = StatusGreen, size = 6)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = activeEmail,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else {
                    Text(
                        text = "No active email",
                        style = MaterialTheme.typography.bodySmall,
                        color = StatusRed.copy(alpha = 0.8f)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = onEditClick, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Outlined.Edit, null, Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Outlined.Delete, null, Modifier.size(16.dp),
                        tint = StatusRed.copy(alpha = 0.6f))
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CountBadge(emailCount, PrimaryBlue, PrimaryBlueSubtle)
            CountBadge(availableCount, StatusGreen, StatusGreenBg)
            if (limitedCount > 0) {
                CountBadge(limitedCount, StatusRed, StatusRedBg)
            }
        }
    }
}

@Composable
fun ModernEmailCard(
    emailAddress: String,
    status: EmailStatus,
    availableAt: Long?,
    toolNames: List<String>,
    onLimitClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GradientIconBox(
                gradient = if (status == EmailStatus.AVAILABLE)
                    Gradients.greenGradient else Gradients.redGradient,
                size = 44
            ) {
                Icon(
                    if (status == EmailStatus.AVAILABLE) Icons.Default.MarkEmailRead
                    else Icons.Default.MarkEmailUnread,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = emailAddress,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (toolNames.isNotEmpty()) {
                    Text(
                        text = toolNames.joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            AnimatedStatusChip(status = status)
        }

        if (status == EmailStatus.LIMITED && availableAt != null) {
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(StatusAmberBg.copy(alpha = 0.3f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Timer,
                    contentDescription = null,
                    tint = StatusAmber,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Available in ${DateTimeUtil.formatRelative(availableAt)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = StatusAmber
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (status == EmailStatus.AVAILABLE) {
                TextButton(
                    onClick = onLimitClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = StatusAmber)
                ) {
                    Icon(Icons.Outlined.Block, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Limit", style = MaterialTheme.typography.labelLarge)
                }
            }
            TextButton(onClick = onEditClick) {
                Icon(Icons.Outlined.Edit, null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Edit", style = MaterialTheme.typography.labelLarge)
            }
            TextButton(
                onClick = onDeleteClick,
                colors = ButtonDefaults.textButtonColors(contentColor = StatusRed)
            ) {
                Icon(Icons.Outlined.Delete, null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Delete", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

