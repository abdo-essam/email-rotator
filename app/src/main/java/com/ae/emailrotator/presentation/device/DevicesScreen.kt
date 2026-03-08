package com.ae.emailrotator.presentation.device

import com.ae.emailrotator.presentation.components.ConfirmDeleteDialog
import com.ae.emailrotator.presentation.components.DeviceCard
import com.ae.emailrotator.presentation.components.EmptyStateIllustration
import com.ae.emailrotator.presentation.navigation.NavRoutes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesScreen(
    navController: NavController,
    viewModel: DevicesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    if (uiState.showAddSheet) {
        AddEditDeviceSheet(
            editingDevice = uiState.editingDevice,
            onSave = { name, type -> viewModel.saveDevice(name, type) },
            onDismiss = { viewModel.dismissSheet() }
        )
    }

    uiState.deleteDialogDevice?.let { device ->
        ConfirmDeleteDialog(
            title = "Delete Device",
            message = "Delete \"${device.name}\"? All tools inside will also be deleted.",
            onConfirm = { viewModel.deleteDevice(device.id) },
            onDismiss = { viewModel.dismissDeleteDialog() }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Devices", style = MaterialTheme.typography.headlineSmall)
                        Text(
                            "Your Mac & Windows machines",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.showAddSheet() },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("Add Device") }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.devicesWithTools.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding)) {
                EmptyStateIllustration(
                    icon = Icons.Default.Devices,
                    title = "No devices yet",
                    subtitle = "Add your first Mac or Windows device"
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(uiState.devicesWithTools, key = { "dev_${it.device.id}" }) { dwt ->
                    DeviceCard(
                        deviceName = dwt.device.name,
                        deviceType = dwt.device.type,
                        toolCount = dwt.tools.size,
                        totalEmails = dwt.totalEmails,
                        availableEmails = dwt.totalAvailable,
                        limitedEmails = dwt.totalLimited,
                        onClick = { navController.navigate(NavRoutes.deviceDetail(dwt.device.id)) },
                        onEditClick = { viewModel.showEditSheet(dwt.device) },
                        onDeleteClick = { viewModel.showDeleteDialog(dwt.device) }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}