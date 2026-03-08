package com.ae.emailrotator.presentation.email

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.ae.emailrotator.presentation.components.*
import com.ae.emailrotator.presentation.navigation.NavRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailManagementScreen(
    navController: NavController,
    viewModel: EmailManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    uiState.limitDialogEmail?.let { email ->
        LimitEmailDialog(
            emailAddress = email.address,
            onConfirm = { timestamp -> viewModel.limitEmail(email.id, timestamp) },
            onDismiss = { viewModel.dismissLimitDialog() }
        )
    }

    uiState.deleteDialogEmail?.let { email ->
        ConfirmDeleteDialog(
            title = "Delete Email",
            message = "Are you sure you want to delete ${email.address}? It will be removed from all assigned tools.",
            onConfirm = { viewModel.deleteEmail(email.id) },
            onDismiss = { viewModel.dismissDeleteDialog() }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Emails") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(NavRoutes.ADD_EMAIL) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Email")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search emails...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.large
            )

            if (uiState.allTools.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.selectedToolFilter == null,
                            onClick = { viewModel.updateToolFilter(null) },
                            label = { Text("All") }
                        )
                    }
                    items(uiState.allTools) { tool ->
                        FilterChip(
                            selected = uiState.selectedToolFilter == tool.id,
                            onClick = {
                                viewModel.updateToolFilter(
                                    if (uiState.selectedToolFilter == tool.id) null
                                    else tool.id
                                )
                            },
                            label = { Text(tool.name) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.emails.isEmpty()) {
                EmptyState(
                    icon = {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    },
                    title = "No emails yet",
                    subtitle = "Add your first email to get started"
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.emails, key = { it.id }) { email ->
                        EmailCard(
                            emailAddress = email.address,
                            status = email.status,
                            availableAt = email.availableAt,
                            toolNames = uiState.allTools.filter { it.id in email.assignedToolIds }.map { it.name },
                            onLimitClick = { viewModel.showLimitDialog(email) },
                            onEditClick = { navController.navigate(NavRoutes.editEmail(email.id)) },
                            onDeleteClick = { viewModel.showDeleteDialog(email) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}
