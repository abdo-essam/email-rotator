package com.ae.emailrotator.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.presentation.theme.*
import com.ae.emailrotator.util.DateTimeUtil

@Composable
fun CurrentEmailCard(
    email: Email?,
    toolName: String,
    queueSize: Int,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GradientBox(
                gradient = Brush.linearGradient(
                    if (email != null) listOf(Green500, Teal500) else listOf(Red500, Amber500)
                ),
                size = 48.dp
            ) {
                Icon(
                    if (email != null) Icons.Filled.MarkEmailRead else Icons.Filled.MarkEmailUnread,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    toolName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (email != null) {
                    Text(
                        email.address,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Text(
                        "No email available",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Red500
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                StatusChip(email?.status ?: EmailStatus.LIMITED)
                Spacer(Modifier.height(4.dp))
                Text(
                    "$queueSize in queue",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EmailListCard(
    email: Email,
    onLimitClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GradientBox(
                gradient = Brush.linearGradient(
                    if (email.status == EmailStatus.AVAILABLE)
                        listOf(Green500, Teal500)
                    else listOf(Red500, Amber500)
                ),
                size = 40.dp
            ) {
                Icon(
                    if (email.status == EmailStatus.AVAILABLE)
                        Icons.Filled.MarkEmailRead
                    else Icons.Filled.MarkEmailUnread,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    email.address,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Tool badge
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            email.tool.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (email.status == EmailStatus.LIMITED && email.availableAt != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.Timer,
                                null,
                                Modifier.size(12.dp),
                                tint = Amber500
                            )
                            Spacer(Modifier.width(3.dp))
                            Text(
                                DateTimeUtil.formatCountdown(email.availableAt),
                                style = MaterialTheme.typography.labelSmall,
                                color = Amber500,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            StatusChip(email.status)
        }

        Spacer(Modifier.height(10.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (email.status == EmailStatus.AVAILABLE) {
                TextButton(
                    onClick = onLimitClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = Amber500),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Outlined.Block, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Limit")
                }
            }
            TextButton(
                onClick = onEditClick,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Outlined.Edit, null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Edit")
            }
            TextButton(
                onClick = onDeleteClick,
                colors = ButtonDefaults.textButtonColors(contentColor = Red500),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Outlined.Delete, null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Delete")
            }
        }
    }
}
