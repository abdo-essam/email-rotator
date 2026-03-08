package com.ae.emailrotator.presentation.email

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    uiState.limitDialogEmail?.let { email ->
        LimitEmailBottomSheet(
            emailAddress = email.address,
            onConfirm = { viewModel.limitEmail(email.id, it) },
            onDismiss = { viewModel.dismissLimitDialog() }
        )
    }

    uiState.deleteDialogEmail?.let { email ->
        ConfirmDeleteDialog(
            title = "Delete Email",
            message = "Delete \"${email.address}\"? It will be removed from all assigned tools.",
            onConfirm = { viewModel.deleteEmail(email.id) },
            onDismiss = { viewModel.dismissDeleteDialog() }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Emails", style = MaterialTheme.typography.headlineSmall)
                        Text(
                            "${uiState.emails.size} email(s)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(NavRoutes.ADD_EMAIL) },
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Add Email") }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // Search
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search emails...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Clear, "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            // Tool filter chips
            if (uiState.allTools.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.selectedToolFilter == null,
                            onClick = { viewModel.updateToolFilter(null) },
                            label = { Text("All") },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    items(uiState.allTools) { tool ->
                        FilterChip(
                            selected = uiState.selectedToolFilter == tool.id,
                            onClick = {
                                viewModel.updateToolFilter(
                                    if (uiState.selectedToolFilter == tool.id) null else tool.id
                                )
                            },
                            label = { Text(tool.name) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.emails.isEmpty()) {
                EmptyStateIllustration(
                    icon = Icons.Default.Email,
                    title = "No emails found",
                    subtitle = if (uiState.searchQuery.isNotBlank()) "Try a different search"
                    else "Add your first email to get started"
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.emails, key = { it.id }) { email ->
                        val toolNames = uiState.allTools
                            .filter { it.id in email.assignedToolIds }
                            .map { it.name }

                        ModernEmailCard(
                            emailAddress = email.address,
                            status = email.status,
                            availableAt = email.availableAt,
                            toolNames = toolNames,
                            onLimitClick = { viewModel.showLimitDialog(email) },
                            onEditClick = { navController.navigate(NavRoutes.editEmail(email.id)) },
                            onDeleteClick = { viewModel.showDeleteDialog(email) }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}