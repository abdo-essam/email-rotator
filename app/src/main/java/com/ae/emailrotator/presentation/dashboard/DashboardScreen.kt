package com.ae.emailrotator.presentation.dashboard

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ae.emailrotator.R
import com.ae.emailrotator.domain.model.DashboardStats
import com.ae.emailrotator.domain.model.DayAvailability
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.presentation.components.*
import com.ae.emailrotator.presentation.theme.*
import com.ae.emailrotator.util.DateTimeUtil
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToVerification: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(state.snackbar) {
        state.snackbar?.let {
            snackbarHost.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    // Limit bottom sheet
    state.limitEmail?.let { email ->
        LimitBottomSheet(
            emailAddress = email.address,
            isUpdate = false,
            onConfirm = { availableAt ->
                viewModel.limitEmail(email.id, availableAt)
            },
            onDismiss = { viewModel.dismissLimit() }
        )
    }

    // Update limit bottom sheet
    state.updateLimitEmail?.let { email ->
        LimitBottomSheet(
            emailAddress = email.address,
            currentAvailableAt = email.availableAt,
            isUpdate = true,
            onConfirm = { newAvailableAt ->
                viewModel.updateLimitTime(email.id, newAvailableAt)
            },
            onDismiss = { viewModel.dismissUpdateLimit() }
        )
    }

    // Date picker dialog
    if (state.showDatePicker) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val selectedCal = Calendar.getInstance().apply {
                    set(year, month, day, 0, 0, 0)
                }
                viewModel.selectDate(selectedCal.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnDismissListener { viewModel.dismissDatePicker() }
        }.show()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHost) },
        topBar = { DashboardTopBar() }
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
                // Stats Overview
                item(key = "stats_section") {
                    StatsSection(
                        stats = state.stats,
                        onVerificationClick = onNavigateToVerification
                    )
                }

                // Date availability selector
                item(key = "date_selector") {
                    DateAvailabilitySection(
                        selectedAvailability = state.selectedDateAvailability,
                        onSelectDate = { viewModel.showDatePicker() },
                        onClearDate = { viewModel.clearDateSelection() }
                    )
                }

                // Per-tool sections
                items(
                    items = state.toolStates,
                    key = { "tool_section_${it.tool.id}" }
                ) { toolState ->
                    ToolSection(
                        toolState = toolState,
                        onLimitEmail = { viewModel.showLimit(it) },
                        onUpdateLimit = { viewModel.showUpdateLimit(it) }
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

        // Tool usage
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
                    activeEmails = toolStat.activeEmails,
                    limitedEmails = toolStat.limitedEmails
                )
            }
        }
    }
}

// ─── Date Availability Section ─────────────────────────────────────────────

@Composable
private fun DateAvailabilitySection(
    selectedAvailability: DayAvailability?,
    onSelectDate: () -> Unit,
    onClearDate: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background == Slate950

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Outlined.CalendarMonth,
                null,
                tint = Purple500,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.dashboard_availability_calendar),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            OutlinedButton(
                onClick = onSelectDate,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    Icons.Default.DateRange,
                    null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.dashboard_select_date),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        if (selectedAvailability != null) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Purple500.copy(alpha = 0.15f)
                    else Color(0xFFF3E8FF)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = selectedAvailability.dateFormatted,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Purple400 else Purple500
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = stringResource(
                                R.string.dashboard_emails_on_day,
                                selectedAvailability.count
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onClearDate) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (selectedAvailability.emails.isNotEmpty()) {
                    HorizontalDivider(
                        color = if (isDark) Purple500.copy(alpha = 0.2f)
                        else Purple500.copy(alpha = 0.1f)
                    )
                    Column(Modifier.padding(14.dp)) {
                        selectedAvailability.emails.forEach { email ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.Email,
                                    null,
                                    tint = if (isDark) Purple400 else Purple500,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = email.address,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1f)
                                )
                                if (email.availableAt != null) {
                                    Text(
                                        text = DateTimeUtil.formatTime(email.availableAt),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Tool Section ──────────────────────────────────────────────────────────

@Composable
private fun ToolSection(
    toolState: ToolEmailState,
    onLimitEmail: (Email) -> Unit,
    onUpdateLimit: (Email) -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background == Slate950

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ToolSectionHeader(toolName = toolState.tool.name)

        // Current email card
        CurrentEmailCard(
            email = toolState.availableEmails.firstOrNull(),
            toolName = stringResource(
                R.string.dashboard_current_email_label,
                toolState.tool.name
            ),
            queueSize = toolState.availableEmails.size,
            nextComingEmail = toolState.nextComingEmail
        )

        // Available Emails Section
        if (toolState.availableEmails.isNotEmpty()) {
            SectionLabel(
                title = stringResource(R.string.dashboard_available_emails),
                count = toolState.availableEmails.size,
                color = if (isDark) Green400 else Green600
            )
            Text(
                text = stringResource(R.string.dashboard_tap_to_limit),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 2.dp)
            )
            toolState.availableEmails.forEach { email ->
                AvailableEmailItem(
                    email = email,
                    onClick = { onLimitEmail(email) }
                )
            }
        }

        // Limited Emails Section
        if (toolState.limitedEmails.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            SectionLabel(
                title = stringResource(R.string.dashboard_limited_emails),
                count = toolState.limitedEmails.size,
                color = if (isDark) Red400 else Red500
            )
            toolState.limitedEmails.forEach { email ->
                LimitedEmailItem(
                    email = email,
                    onClick = { onUpdateLimit(email) }
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(
    title: String,
    count: Int,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(color.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
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