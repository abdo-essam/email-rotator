package com.ae.emailrotator.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ae.emailrotator.domain.model.HistoryAction
import com.ae.emailrotator.domain.model.UsageHistory
import com.ae.emailrotator.presentation.components.EmptyState
import com.ae.emailrotator.presentation.theme.StatusAvailable
import com.ae.emailrotator.presentation.theme.StatusLimited
import com.ae.emailrotator.presentation.theme.Warning
import com.ae.emailrotator.util.DateTimeUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Usage History") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.history.isEmpty()) {
            Box(modifier = Modifier.padding(padding)) {
                EmptyState(
                    icon = {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    },
                    title = "No history yet",
                    subtitle = "Actions will appear here when emails are rotated or limited"
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.history, key = { it.id }) { entry ->
                    HistoryEntryCard(entry = entry)
                }
            }
        }
    }
}

@Composable
fun HistoryEntryCard(entry: UsageHistory) {
    val (icon, iconColor, label) = remember(entry.action) {
        when (entry.action) {
            HistoryAction.ACTIVATED -> Triple(
                Icons.Default.CheckCircle, StatusAvailable, "Activated"
            )
            HistoryAction.LIMITED -> Triple(
                Icons.Default.Block, StatusLimited, "Limited"
            )
            HistoryAction.BECAME_AVAILABLE -> Triple(
                Icons.Default.Refresh, Warning, "Became Available"
            )
            HistoryAction.ROTATED_OUT -> Triple(
                Icons.Default.SwapHoriz, Color.Gray, "Rotated Out"
            )
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.emailAddress,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Row {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = iconColor.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = iconColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = entry.toolName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Text(
                text = DateTimeUtil.formatDateTime(entry.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}
