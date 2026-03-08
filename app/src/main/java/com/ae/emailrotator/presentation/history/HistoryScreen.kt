package com.ae.emailrotator.presentation.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ae.emailrotator.domain.model.HistoryAction
import com.ae.emailrotator.domain.model.UsageHistory
import com.ae.emailrotator.presentation.components.EmptyStateIllustration
import com.ae.emailrotator.presentation.components.GradientBadge
import com.ae.emailrotator.presentation.components.GradientIconBox
import com.ae.emailrotator.presentation.history.components.HistoryEntryCard
import com.ae.emailrotator.presentation.theme.AccentOrange
import com.ae.emailrotator.presentation.theme.Gradients
import com.ae.emailrotator.presentation.theme.StatusAmber
import com.ae.emailrotator.presentation.theme.StatusGreen
import com.ae.emailrotator.presentation.theme.StatusRed
import com.ae.emailrotator.util.DateTimeUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: HistoryViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("History", style = MaterialTheme.typography.headlineSmall)
                        Text(
                            "Email rotation activity log",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.history.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding)) {
                EmptyStateIllustration(
                    icon = Icons.Default.History,
                    title = "No history yet",
                    subtitle = "Activity will appear here when emails are rotated"
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Group by date
                val grouped = uiState.history.groupBy { DateTimeUtil.formatDate(it.timestamp) }

                grouped.forEach { (date, entries) ->
                    item {
                        Text(
                            date,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(entries, key = { it.id }) { entry ->
                        HistoryEntryCard(entry = entry)
                    }
                }
            }
        }
    }
}

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
