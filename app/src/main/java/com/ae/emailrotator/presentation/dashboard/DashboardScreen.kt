package com.ae.emailrotator.presentation.dashboard

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.util.DateTimeUtil
import androidx.compose.animation.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import com.ae.emailrotator.domain.model.DeviceType
import com.ae.emailrotator.domain.model.DeviceWithTools
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.ToolWithEmails
import com.ae.emailrotator.presentation.components.AnimatedStatusChip
import com.ae.emailrotator.presentation.components.CountBadge
import com.ae.emailrotator.presentation.components.EmptyStateIllustration
import com.ae.emailrotator.presentation.components.GlassCard
import com.ae.emailrotator.presentation.components.GradientIconBox
import com.ae.emailrotator.presentation.components.LimitEmailBottomSheet
import com.ae.emailrotator.presentation.components.PulsingDot
import com.ae.emailrotator.presentation.navigation.NavRoutes
import com.ae.emailrotator.presentation.theme.AccentPurple
import com.ae.emailrotator.presentation.theme.AccentTeal
import com.ae.emailrotator.presentation.theme.Gradients
import com.ae.emailrotator.presentation.theme.PrimaryBlue
import com.ae.emailrotator.presentation.theme.StatusAmber
import com.ae.emailrotator.presentation.theme.StatusGreen
import com.ae.emailrotator.presentation.theme.StatusGreenBg
import com.ae.emailrotator.presentation.theme.StatusRed
import com.ae.emailrotator.presentation.theme.StatusRedBg


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    if (uiState.limitDialogEmailId != null && uiState.limitDialogEmailAddress != null) {
        LimitEmailBottomSheet(
            emailAddress = uiState.limitDialogEmailAddress!!,
            onConfirm = { viewModel.limitEmail(uiState.limitDialogEmailId!!, it) },
            onDismiss = { viewModel.dismissLimitDialog() }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Email Rotator",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Manage your email rotation",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onToggleDarkMode) {
                        Icon(
                            if (isDarkMode) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                            contentDescription = "Toggle theme"
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
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Stats Overview
                item { StatsOverviewSection(uiState) }

                // Quick Device Overview
                if (uiState.devicesWithTools.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Devices",
                            subtitle = "${uiState.totalDevices} device(s)",
                            icon = Icons.Default.Devices
                        )
                    }

                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.devicesWithTools, key = { it.device.id }) { dwt ->
                                DeviceQuickCard(
                                    deviceWithTools = dwt,
                                    onClick = {
                                        navController.navigate(NavRoutes.deviceDetail(dwt.device.id))
                                    }
                                )
                            }
                        }
                    }
                }

                // Active Emails per Tool
                item {
                    SectionHeader(
                        title = "Active Rotations",
                        subtitle = "Current email assignments",
                        icon = Icons.Default.SwapHoriz
                    )
                }

                if (uiState.devicesWithTools.isEmpty()) {
                    item {
                        EmptyStateIllustration(
                            icon = Icons.Default.Devices,
                            title = "No devices yet",
                            subtitle = "Add a device to start managing your email rotations"
                        )
                    }
                } else {
                    uiState.devicesWithTools.forEach { dwt ->
                        if (dwt.tools.isNotEmpty()) {
                            item {
                                Text(
                                    text = "${dwt.device.type.icon} ${dwt.device.name}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                )
                            }
                            items(dwt.tools, key = { it.tool.id }) { twe ->
                                ActiveRotationCard(
                                    toolWithEmails = twe,
                                    deviceName = dwt.device.name,
                                    onToolClick = {
                                        navController.navigate(NavRoutes.toolDetail(twe.tool.id))
                                    }
                                )
                            }
                        }
                    }
                }

                // Email Status Table
                if (uiState.allEmails.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        SectionHeader(
                            title = "All Emails",
                            subtitle = "${uiState.totalEmails} email(s)",
                            icon = Icons.Default.Email
                        )
                    }

                    items(uiState.allEmails, key = { it.id }) { email ->
                        EmailQuickStatusCard(
                            email = email,
                            onLimitClick = {
                                viewModel.showLimitDialog(email.id, email.address)
                            }
                        )
                    }
                }

                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun StatsOverviewSection(uiState: DashboardUiState) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Overview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OverviewStat(
                icon = Icons.Default.Devices,
                value = uiState.totalDevices.toString(),
                label = "Devices",
                gradient = Brush.linearGradient(listOf(PrimaryBlue, AccentPurple))
            )
            OverviewStat(
                icon = Icons.Default.Extension,
                value = uiState.totalTools.toString(),
                label = "Tools",
                gradient = Brush.linearGradient(listOf(AccentPurple, AccentTeal))
            )
            OverviewStat(
                icon = Icons.Default.CheckCircle,
                value = uiState.totalAvailable.toString(),
                label = "Active",
                gradient = Gradients.greenGradient
            )
            OverviewStat(
                icon = Icons.Default.Block,
                value = uiState.totalLimited.toString(),
                label = "Limited",
                gradient = Gradients.redGradient
            )
        }
    }
}

@Composable
fun OverviewStat(icon: ImageVector, value: String, label: String, gradient: Brush) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        GradientIconBox(gradient = gradient, size = 40) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.width(10.dp))
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DeviceQuickCard(deviceWithTools: DeviceWithTools, onClick: () -> Unit) {
    val device = deviceWithTools.device
    val gradient = when (device.type) {
        DeviceType.MAC -> Gradients.macGradient
        DeviceType.WINDOWS -> Gradients.windowsGradient
    }

    Card(
        onClick = onClick,
        modifier = Modifier.width(180.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GradientIconBox(gradient = gradient, size = 36) {
                    Text(device.type.icon, fontSize = 16.sp)
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    device.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MiniStat("${deviceWithTools.tools.size}", "tools", PrimaryBlue)
                MiniStat("${deviceWithTools.totalAvailable}", "active", StatusGreen)
            }
        }
    }
}

@Composable
fun MiniStat(value: String, label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            "$value $label",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ActiveRotationCard(
    toolWithEmails: ToolWithEmails,
    deviceName: String,
    onToolClick: () -> Unit
) {
    Card(
        onClick = onToolClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GradientIconBox(
                gradient = Brush.linearGradient(listOf(AccentPurple, PrimaryBlue)),
                size = 40
            ) {
                Icon(Icons.Default.Extension, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    toolWithEmails.tool.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                val activeEmail = toolWithEmails.currentActiveEmail
                if (activeEmail != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PulsingDot(StatusGreen, 6)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            activeEmail.address,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else {
                    Text(
                        "No active email",
                        style = MaterialTheme.typography.bodySmall,
                        color = StatusRed.copy(alpha = 0.8f)
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    CountBadge(toolWithEmails.availableCount, StatusGreen, StatusGreenBg)
                    if (toolWithEmails.limitedCount > 0)
                        CountBadge(toolWithEmails.limitedCount, StatusRed, StatusRedBg)
                }
            }
        }
    }
}

@Composable
fun EmailQuickStatusCard(email: Email, onLimitClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (email.status == EmailStatus.AVAILABLE) StatusGreen else StatusRed
                    )
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    email.address,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (email.status == EmailStatus.LIMITED && email.availableAt != null) {
                    Text(
                        "Available: ${DateTimeUtil.formatRelative(email.availableAt)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = StatusAmber
                    )
                }
            }
            AnimatedStatusChip(email.status)
            if (email.status == EmailStatus.AVAILABLE) {
                Spacer(Modifier.width(4.dp))
                IconButton(onClick = onLimitClick, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Outlined.Block, null,
                        modifier = Modifier.size(16.dp),
                        tint = StatusAmber
                    )
                }
            }
        }
    }
}