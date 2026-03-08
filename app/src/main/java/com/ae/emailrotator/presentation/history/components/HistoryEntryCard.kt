package com.ae.emailrotator.presentation.history.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ae.emailrotator.domain.model.HistoryAction
import com.ae.emailrotator.domain.model.UsageHistory
import com.ae.emailrotator.presentation.components.GradientBadge
import com.ae.emailrotator.presentation.components.GradientIconBox
import com.ae.emailrotator.presentation.theme.AccentOrange
import com.ae.emailrotator.presentation.theme.Gradients
import com.ae.emailrotator.presentation.theme.StatusAmber
import com.ae.emailrotator.presentation.theme.StatusGreen
import com.ae.emailrotator.presentation.theme.StatusRed
import com.ae.emailrotator.util.DateTimeUtil

@Composable
fun HistoryEntryCard(entry: UsageHistory) {
    val (icon, iconColor, label) = remember(entry.action) {
        when (entry.action) {
            HistoryAction.ACTIVATED -> Triple(Icons.Default.CheckCircle, StatusGreen, "Activated")
            HistoryAction.LIMITED -> Triple(Icons.Default.Block, StatusRed, "Limited")
            HistoryAction.BECAME_AVAILABLE -> Triple(Icons.Default.Refresh, StatusAmber, "Available")
            HistoryAction.ROTATED_OUT -> Triple(Icons.Default.SwapHoriz, Color.Gray, "Rotated Out")
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GradientIconBox(
                gradient = when (entry.action) {
                    HistoryAction.ACTIVATED -> Gradients.greenGradient
                    HistoryAction.LIMITED -> Gradients.redGradient
                    HistoryAction.BECAME_AVAILABLE -> kotlin.run {
                        androidx.compose.ui.graphics.Brush.linearGradient(listOf(StatusAmber, AccentOrange))
                    }
                    HistoryAction.ROTATED_OUT -> kotlin.run {
                        androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color.Gray, Color.DarkGray))
                    }
                },
                size = 38
            ) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(18.dp))
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    entry.emailAddress,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GradientBadge(
                        text = label,
                        gradient = when (entry.action) {
                            HistoryAction.ACTIVATED -> Gradients.greenGradient
                            HistoryAction.LIMITED -> Gradients.redGradient
                            else -> androidx.compose.ui.graphics.Brush.linearGradient(
                                listOf(iconColor, iconColor)
                            )
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "${entry.toolName} · ${entry.deviceName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                DateTimeUtil.formatTimeAgo(entry.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
