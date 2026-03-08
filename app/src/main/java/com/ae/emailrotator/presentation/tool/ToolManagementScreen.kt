package com.ae.emailrotator.presentation.tool

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
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.presentation.components.ConfirmDeleteDialog
import com.ae.emailrotator.presentation.components.EmptyState
import com.ae.emailrotator.presentation.components.ToolCard
import com.ae.emailrotator.presentation.navigation.NavRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolManagementScreen(
    navController: NavController,
    viewModel: ToolManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    uiState.deleteDialogTool?.let { twe ->
        ConfirmDeleteDialog(
            title = "Delete Tool",
            message = "Are you sure you want to delete ${twe.tool.name}?",
            onConfirm = { viewModel.deleteTool(twe.tool.id) },
            onDismiss = { viewModel.dismissDeleteDialog() }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Tools") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(NavRoutes.ADD_TOOL) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Tool")
            }
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
        } else if (uiState.toolsWithEmails.isEmpty()) {
            Box(modifier = Modifier.padding(padding)) {
                EmptyState(
                    icon = {
                        Icon(
                            Icons.Default.Build,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    },
                    title = "No tools yet",
                    subtitle = "Add your first tool to start managing emails"
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.toolsWithEmails, key = { it.tool.id }) { twe ->
                    val availableCount = twe.emails.count {
                        it.email.status == EmailStatus.AVAILABLE
                    }
                    ToolCard(
                        toolName = twe.tool.name,
                        activeEmail = twe.currentActiveEmail?.address,
                        emailCount = twe.emails.size,
                        availableCount = availableCount,
                        onEditClick = {
                            navController.navigate(NavRoutes.editTool(twe.tool.id))
                        },
                        onDeleteClick = { viewModel.showDeleteDialog(twe) }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}
