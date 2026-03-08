package com.ae.emailrotator.presentation.device

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ae.emailrotator.domain.model.DeviceType
import com.ae.emailrotator.presentation.components.ConfirmDeleteDialog
import com.ae.emailrotator.presentation.components.EmptyStateIllustration
import com.ae.emailrotator.presentation.components.GlassCard
import com.ae.emailrotator.presentation.components.GradientIconBox
import com.ae.emailrotator.presentation.components.ModernToolCard
import com.ae.emailrotator.presentation.navigation.NavRoutes
import com.ae.emailrotator.presentation.theme.AccentPurple
import com.ae.emailrotator.presentation.theme.Gradients
import com.ae.emailrotator.presentation.theme.PrimaryBlue
import com.ae.emailrotator.presentation.theme.StatusGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailScreen(
    navController: NavController,
    deviceId: Long,
    viewModel: DeviceDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(deviceId) { viewModel.loadDevice(deviceId) }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    uiState.deleteDialogTool?.let { twe ->
        ConfirmDeleteDialog(
            title = "Delete Tool",
            message = "Delete \"${twe.tool.name}\"? All email assignments will be removed.",
            onConfirm = { viewModel.deleteTool(twe.tool.id) },
            onDismiss = { viewModel.dismissDeleteToolDialog() }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    val dwt = uiState.deviceWithTools
                    if (dwt != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val gradient = when (dwt.device.type) {
                                DeviceType.MAC -> Gradients.macGradient
                                DeviceType.WINDOWS -> Gradients.windowsGradient
                            }
                            GradientIconBox(gradient = gradient, size = 36) {
                                Text(dwt.device.type.icon, fontSize = 16.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    dwt.device.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    dwt.device.type.displayName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        Text("Device Details")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(NavRoutes.addTool(deviceId)) },
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Add Tool") }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val dwt = uiState.deviceWithTools
            if (dwt == null) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Device not found")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Stats Card
                    item {
                        GlassCard(Modifier.fillMaxWidth()) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "${dwt.tools.size}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBlue
                                    )
                                    Text("Tools", style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "${dwt.totalEmails}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentPurple
                                    )
                                    Text("Emails", style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "${dwt.totalAvailable}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusGreen
                                    )
                                    Text("Active", style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    // Tools
                    item {
                        Text(
                            "Tools",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    if (dwt.tools.isEmpty()) {
                        item {
                            EmptyStateIllustration(
                                icon = Icons.Default.Extension,
                                title = "No tools yet",
                                subtitle = "Add your first tool to this device"
                            )
                        }
                    } else {
                        items(dwt.tools, key = { it.tool.id }) { twe ->
                            ModernToolCard(
                                toolName = twe.tool.name,
                                activeEmail = twe.currentActiveEmail?.address,
                                emailCount = twe.emails.size,
                                availableCount = twe.availableCount,
                                limitedCount = twe.limitedCount,
                                onClick = { navController.navigate(NavRoutes.toolDetail(twe.tool.id)) },
                                onEditClick = { navController.navigate(NavRoutes.editTool(twe.tool.id)) },
                                onDeleteClick = { viewModel.showDeleteToolDialog(twe) }
                            )
                        }
                    }

                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}