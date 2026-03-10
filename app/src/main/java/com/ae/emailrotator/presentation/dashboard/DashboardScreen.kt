package com.ae.emailrotator.presentation.dashboard

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
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
            onConfirm = { viewModel.limitEmail(email.id, it) },
            onDismiss = { viewModel.dismissLimit() }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHost) },
        topBar = {
            DashboardTopBar(
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode
            )
        }
    ) { padding ->
        if (state.isLoading) {
            LoadingIndicator(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item(key = "stats_section") {
                    StatsSection(
                        stats = state.stats,
                        onVerificationClick = onNavigateToVerification
                    )
                }

                item(key = "tools_divider") {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                    )
                }

                items(
                    items = state.toolStates,
                    key = { "tool_section_${it.tool.id}" }
                ) { toolState ->
                    ToolSection(
                        toolState = toolState,
                        onLimitClick = { viewModel.showLimit(toolState.current!!) }
                    )
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.10f)
                    )
                }

                item(key = "bottom_space") {
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
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
        actions = {
            IconButton(onClick = onToggleDarkMode) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Outlined.LightMode
                    else Icons.Outlined.DarkMode,
                    contentDescription = stringResource(R.string.common_toggle_theme)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun StatsSection(
    stats: DashboardStats,
    onVerificationClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Overview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.stat_total_emails),
                value = stats.totalEmails.toString(),
                icon = Icons.Outlined.Email,
                color = Blue500
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.stat_active_emails),
                value = stats.activeEmails.toString(),
                icon = Icons.Outlined.CheckCircle,
                color = Green500
            )
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.stat_limited_emails),
                value = stats.limitedEmails.toString(),
                icon = Icons.Outlined.Block,
                color = Red500
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.stat_needs_verification),
                value = stats.needsVerificationEmails.toString(),
                icon = Icons.Outlined.HelpOutline,
                color = Amber500,
                onClick = if (stats.needsVerificationEmails > 0) onVerificationClick else null
            )
        }
        if (stats.toolStats.isNotEmpty()) {
            Text(
                text = stringResource(R.string.stat_tool_usage),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp)
            )
            stats.toolStats.forEach { toolStat ->
                ToolStatRow(
                    toolName = toolStat.toolName,
                    total = toolStat.totalEmails,
                    active = toolStat.activeEmails
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    onClick: (() -> Unit)? = null
) {
    Card(
        onClick = onClick ?: {},
        modifier = modifier,
        enabled = onClick != null,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
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
}

@Composable
private fun ToolStatRow(
    toolName: String,
    total: Int,
    active: Int
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = toolName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.stat_emails_count, total),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Green500.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "$active active",
                    style = MaterialTheme.typography.labelSmall,
                    color = Green500,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

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
            LimitCurrentButton(
                email = toolState.current,
                onLimitClick = onLimitClick
            )
        }

        if (toolState.queue.size > 1) {
            Text(
                text = stringResource(R.string.dashboard_queue_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
            toolState.queue.drop(1).forEach { email ->
                QueueEmailItem(email = email)
            }
        }
    }
}

@Composable
private fun ToolSectionHeader(toolName: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        GradientBox(
            gradient = Brush.linearGradient(listOf(Blue500, Purple500)),
            size = 36.dp,
            cornerRadius = 10.dp
        ) {
            Icon(
                Icons.Default.Build,
                null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = toolName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun LimitCurrentButton(
    email: Email,
    onLimitClick: () -> Unit
) {
    Button(
        onClick = onLimitClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Amber500.copy(alpha = 0.15f),
            contentColor = Amber500
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Icon(Icons.Outlined.Block, null, Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.dashboard_limit_current),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun QueueEmailItem(email: Email) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusDot(email.status)
            Spacer(Modifier.width(12.dp))
            Text(
                text = email.address,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.dashboard_ready),
                style = MaterialTheme.typography.labelSmall,
                color = Green500,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}