package com.ae.emailrotator.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ae.emailrotator.R
import com.ae.emailrotator.domain.model.DashboardStats
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.presentation.components.*
import com.ae.emailrotator.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToVerification: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(state.snackbar) {
        state.snackbar?.let {
            snackbarHost.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    state.limitEmail?.let { email ->
        LimitBottomSheet(
            emailAddress = email.address,
            onConfirm = { availableAt ->
                viewModel.limitEmail(email.id, email.toolId, availableAt)
            },
            onDismiss = { viewModel.dismissLimit() }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHost) },
        topBar = {
            DashboardTopBar()
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 32.dp
                ),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // ── Stats Overview ──────────────────────────────────────
                item(key = "stats_section") {
                    StatsSection(
                        stats = state.stats,
                        onVerificationClick = onNavigateToVerification
                    )
                }

                // ── Per-tool sections ───────────────────────────────────
                items(
                    items = state.toolStates,
                    key = { "tool_section_${it.tool.id}" }
                ) { toolState ->
                    ToolSection(
                        toolState = toolState,
                        onLimitClick = { viewModel.showLimit(toolState.current!!) }
                    )
                }
            }
        }
    }
}

// ─── Top Bar ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar() {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = stringResource(R.string.dashboard_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.dashboard_subtitle),
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

// ─── Stats Section ─────────────────────────────────────────────────────────

@Composable
private fun StatsSection(
    stats: DashboardStats,
    onVerificationClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Overview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        // Row 1 – Total / Active
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.stat_total_emails),
                value = stats.totalEmails.toString(),
                icon = Icons.Outlined.Email,
                tintColor = Blue500
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.stat_active_emails),
                value = stats.activeEmails.toString(),
                icon = Icons.Outlined.CheckCircle,
                tintColor = Green500
            )
        }

        // Row 2 – Limited / Needs Verification
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.stat_limited_emails),
                value = stats.limitedEmails.toString(),
                icon = Icons.Outlined.Block,
                tintColor = Red500
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.stat_needs_verification),
                value = stats.needsVerificationEmails.toString(),
                icon = Icons.Outlined.HelpOutline,
                tintColor = Amber500,
                onClick = if (stats.needsVerificationEmails > 0) onVerificationClick else null
            )
        }

        // Tool Usage
        if (stats.toolStats.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.stat_tool_usage),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            stats.toolStats.forEach { toolStat ->
                ToolUsageCard(
                    toolName = toolStat.toolName,
                    totalEmails = toolStat.totalEmails,
                    activeEmails = toolStat.activeEmails
                )
            }
        }
    }
}

// ─── Tool Section ──────────────────────────────────────────────────────────

@Composable
private fun ToolSection(
    toolState: ToolEmailState,
    onLimitClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ToolSectionHeader(toolName = toolState.tool.name)

        CurrentEmailCard(
            email = toolState.current,
            toolName = stringResource(
                R.string.dashboard_current_email_label,
                toolState.tool.name
            ),
            queueSize = toolState.queue.size
        )

        if (toolState.current != null) {
            LimitCurrentButton(onLimitClick = onLimitClick)
        }

        if (toolState.queue.size > 1) {
            Text(
                text = stringResource(R.string.dashboard_queue_label),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 2.dp)
            )
            toolState.queue.drop(1).forEach { email ->
                QueueEmailItem(email = email)
            }
        }
    }
}

// ─── Tool Section Header ───────────────────────────────────────────────────

@Composable
private fun ToolSectionHeader(toolName: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        GradientBox(
            gradient = Brush.linearGradient(listOf(Blue500, Purple500)),
            size = 34.dp,
            cornerRadius = 10.dp
        ) {
            Icon(
                Icons.Default.Build,
                null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = toolName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

// ─── Limit Current Button ──────────────────────────────────────────────────

@Composable
private fun LimitCurrentButton(onLimitClick: () -> Unit) {
    val isDark = MaterialTheme.colorScheme.background == Slate950
    OutlinedButton(
        onClick = onLimitClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = if (isDark) Amber400 else Amber600
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = Brush.linearGradient(
                listOf(
                    if (isDark) Amber400 else Amber500,
                    if (isDark) Amber400 else Amber500
                )
            )
        )
    ) {
        Icon(Icons.Outlined.Block, null, Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            text = stringResource(R.string.dashboard_limit_current),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ─── Queue Email Item ──────────────────────────────────────────────────────

@Composable
private fun QueueEmailItem(email: Email) {
    val isDark = MaterialTheme.colorScheme.background == Slate950
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (isDark) Slate800 else Slate50)
            .border(1.dp, if (isDark) Slate700 else Slate200, shape)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusDot(email.status)
        Spacer(Modifier.width(10.dp))
        Text(
            text = email.address,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = stringResource(R.string.dashboard_ready),
            style = MaterialTheme.typography.labelSmall,
            color = if (isDark) Green400 else Green600,
            fontWeight = FontWeight.SemiBold
        )
    }
}