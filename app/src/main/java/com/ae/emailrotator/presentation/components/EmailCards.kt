package com.ae.emailrotator.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ae.emailrotator.R
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.presentation.theme.*
import com.ae.emailrotator.util.DateTimeUtil

// ─── Current Email Card ────────────────────────────────────────────────────

@Composable
fun CurrentEmailCard(
    email: Email?,
    toolName: String,
    queueSize: Int,
    nextComingEmail: Email? = null,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == Slate950
    val shape = RoundedCornerShape(16.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isDark) 0.dp else 2.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.05f)
            )
            .clip(shape)
            .background(if (isDark) Slate800 else Color.White)
            .border(1.dp, if (isDark) Slate700 else Slate200, shape)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val isAvailable = email != null
            GradientBox(
                gradient = Brush.linearGradient(
                    if (isAvailable) listOf(Green500, Teal500)
                    else listOf(Red400, Red500)
                ),
                size = 46.dp,
                cornerRadius = 13.dp
            ) {
                Icon(
                    imageVector = if (isAvailable) Icons.Filled.MarkEmailRead
                    else Icons.Filled.MarkEmailUnread,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = toolName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(2.dp))
                if (email != null) {
                    Text(
                        text = email.address,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Text(
                        text = stringResource(R.string.dashboard_no_email),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Red400 else Red500
                    )
                    // Show next coming email
                    if (nextComingEmail != null && nextComingEmail.availableAt != null) {
                        Spacer(Modifier.height(4.dp))
                        NextComingBadge(
                            email = nextComingEmail,
                            isDark = isDark
                        )
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                StatusChip(email?.status ?: EmailStatus.LIMITED)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.dashboard_queue_count, queueSize),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun NextComingBadge(
    email: Email,
    isDark: Boolean
) {
    val (countdown, dateTime) = DateTimeUtil.formatCountdownWithDate(email.availableAt!!)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isDark) Amber500.copy(alpha = 0.15f) else Amber50)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            Icons.Outlined.Schedule,
            null,
            tint = if (isDark) Amber400 else Amber600,
            modifier = Modifier.size(12.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = "Next in $countdown",
            style = MaterialTheme.typography.labelSmall,
            color = if (isDark) Amber400 else Amber600,
            fontWeight = FontWeight.Medium
        )
    }
}

// ─── Available Email Item (Clickable to limit) ─────────────────────────────

@Composable
fun AvailableEmailItem(
    email: Email,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == Slate950
    val shape = RoundedCornerShape(12.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (isDark) Slate800.copy(alpha = 0.6f) else Green50.copy(alpha = 0.5f))
            .border(1.dp, if (isDark) Slate700 else Green100, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusDot(EmailStatus.AVAILABLE)
        Spacer(Modifier.width(10.dp))
        Text(
            text = email.address,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        // Tap to limit hint
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isDark) Amber500.copy(alpha = 0.15f) else Amber50)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                Icons.Outlined.TouchApp,
                null,
                tint = if (isDark) Amber400 else Amber600,
                modifier = Modifier.size(12.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Tap to limit",
                style = MaterialTheme.typography.labelSmall,
                color = if (isDark) Amber400 else Amber600,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ─── Limited Email Item (Shows full countdown + clickable to update) ───────

@Composable
fun LimitedEmailItem(
    email: Email,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == Slate950
    val shape = RoundedCornerShape(12.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (isDark) Slate800.copy(alpha = 0.6f) else Red50.copy(alpha = 0.3f))
            .border(1.dp, if (isDark) Slate700 else Red100, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusDot(EmailStatus.LIMITED)
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = email.address,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (email.availableAt != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = DateTimeUtil.formatDateTime(email.availableAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        // Countdown badge
        if (email.availableAt != null) {
            CountdownBadgeFull(
                availableAt = email.availableAt,
                isDark = isDark
            )
        }
    }
}

@Composable
private fun CountdownBadgeFull(
    availableAt: Long,
    isDark: Boolean
) {
    val countdown = DateTimeUtil.formatCountdown(availableAt)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isDark) Blue500.copy(alpha = 0.15f) else Blue50)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(
            Icons.Outlined.Timer,
            null,
            tint = if (isDark) Blue400 else Blue600,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = countdown,
            style = MaterialTheme.typography.labelMedium,
            color = if (isDark) Blue400 else Blue600,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ─── Email List Card (for Emails Screen) ───────────────────────────────────

@Composable
fun EmailListCard(
    email: Email,
    onLimitClick: () -> Unit,
    onUpdateLimitClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onVerifyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == Slate950
    val shape = RoundedCornerShape(16.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isDark) 0.dp else 2.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.05f)
            )
            .clip(shape)
            .background(if (isDark) Slate800 else Color.White)
            .border(1.dp, if (isDark) Slate700 else Slate200, shape)
            .padding(16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EmailStatusIcon(status = email.status)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = email.address,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ToolNameBadge(toolName = email.toolName)
                    if (email.isLimited && email.availableAt != null) {
                        CountdownBadgeCompact(
                            availableAt = email.availableAt,
                            isDark = isDark
                        )
                    }
                }
                // Show full date/time for limited emails
                if (email.isLimited && email.availableAt != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Available: ${DateTimeUtil.formatFullDateTime(email.availableAt)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            StatusChip(email.status)
        }

        Spacer(Modifier.height(8.dp))

        HorizontalDivider(
            color = if (isDark) Slate700 else Slate200,
            thickness = 0.5.dp
        )

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (email.needsVerification) {
                EmailActionButton(
                    label = stringResource(R.string.action_verify),
                    icon = Icons.Outlined.CheckCircle,
                    color = if (isDark) Green400 else Green600,
                    onClick = onVerifyClick
                )
            }
            if (email.isUsable) {
                EmailActionButton(
                    label = stringResource(R.string.action_limit),
                    icon = Icons.Outlined.Block,
                    color = if (isDark) Amber400 else Amber600,
                    onClick = onLimitClick
                )
            }
            if (email.isLimited) {
                EmailActionButton(
                    label = "Update",
                    icon = Icons.Outlined.Update,
                    color = if (isDark) Blue400 else Blue600,
                    onClick = onUpdateLimitClick
                )
            }
            EmailActionButton(
                label = stringResource(R.string.action_edit),
                icon = Icons.Outlined.Edit,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = onEditClick
            )
            EmailActionButton(
                label = stringResource(R.string.action_delete),
                icon = Icons.Outlined.Delete,
                color = if (isDark) Red400 else Red500,
                onClick = onDeleteClick
            )
        }
    }
}

// ─── Private helpers ───────────────────────────────────────────────────────

@Composable
private fun EmailStatusIcon(status: EmailStatus) {
    val gradient = when (status) {
        EmailStatus.AVAILABLE -> Brush.linearGradient(listOf(Green500, Teal500))
        EmailStatus.LIMITED -> Brush.linearGradient(listOf(Red400, Red500))
        EmailStatus.NEEDS_VERIFICATION -> Brush.linearGradient(listOf(Amber400, Amber500))
    }
    val icon = when (status) {
        EmailStatus.AVAILABLE -> Icons.Filled.MarkEmailRead
        EmailStatus.LIMITED -> Icons.Filled.MarkEmailUnread
        EmailStatus.NEEDS_VERIFICATION -> Icons.Filled.HelpOutline
    }
    GradientBox(gradient = gradient, size = 38.dp, cornerRadius = 11.dp) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun ToolNameBadge(toolName: String) {
    val isDark = MaterialTheme.colorScheme.background == Slate950
    Box(
        Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isDark) Blue500.copy(alpha = 0.18f) else Blue50)
            .border(
                1.dp,
                if (isDark) Blue400.copy(alpha = 0.25f) else Blue100,
                RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = toolName.ifBlank { "—" },
            style = MaterialTheme.typography.labelSmall,
            color = if (isDark) Blue400 else Blue600,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun CountdownBadgeCompact(
    availableAt: Long,
    isDark: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            Icons.Outlined.Timer,
            null,
            Modifier.size(12.dp),
            tint = if (isDark) Amber400 else Amber600
        )
        Text(
            text = DateTimeUtil.formatCountdown(availableAt),
            style = MaterialTheme.typography.labelSmall,
            color = if (isDark) Amber400 else Amber600,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun EmailActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(contentColor = color),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(icon, null, Modifier.size(15.dp))
        Spacer(Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}
