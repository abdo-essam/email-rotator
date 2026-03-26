package com.ae.emailrotator.presentation.emails

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ae.emailrotator.R
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.domain.model.Tool
import com.ae.emailrotator.presentation.components.*
import com.ae.emailrotator.presentation.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailsScreen(
    viewModel: EmailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(state.snackbar) {
        state.snackbar?.let {
            snackbarHost.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    // Add/Edit Sheet
    if (state.showAddSheet) {
        AddEditEmailSheet(
            editingEmail = state.editingEmail,
            onSave = { address, needsVerification ->
                viewModel.saveEmail(address, needsVerification)
            },
            onDismiss = { viewModel.dismissSheet() }
        )
    }

    // Limit Sheet
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

    // Update Limit Sheet
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

    // Delete Dialog
    state.deleteEmail?.let { email ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissDelete() },
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Warning,
                        null,
                        tint = Red500,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        stringResource(R.string.delete_title),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(
                    text = stringResource(R.string.delete_message, email.address),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteEmail(email.globalEmailId) },
                    colors = ButtonDefaults.buttonColors(containerColor = Red500),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.delete_confirm))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.dismissDelete() },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.delete_cancel))
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHost) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.emails_title),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.emails_count, state.emails.size),
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
            FloatingActionButton(
                onClick = { viewModel.showAdd() },
                containerColor = Blue500,
                contentColor = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.emails_add),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            SearchBar(
                query = state.searchQuery,
                onQueryChange = { viewModel.setSearch(it) }
            )

            // Tool Filter
            ToolFilterRow(
                tools = state.tools,
                selectedToolId = state.toolFilter,
                onFilterSelected = { viewModel.setToolFilter(it) }
            )

            Spacer(Modifier.height(8.dp))

            when {
                state.isLoading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                state.emails.isEmpty() -> EmailsEmptyState(
                    hasSearch = state.searchQuery.isNotBlank()
                )
                else -> EmailsList(
                    emails = state.emails,
                    onLimitClick = { viewModel.showLimit(it) },
                    onUpdateLimitClick = { viewModel.showUpdateLimit(it) },
                    onEditClick = { viewModel.showEdit(it) },
                    onDeleteClick = { viewModel.showDelete(it) },
                    onVerifyClick = { viewModel.verifyEmail(it.globalEmailId, it.toolId) }
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text(stringResource(R.string.emails_search_placeholder)) },
        leadingIcon = { Icon(Icons.Default.Search, null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        Icons.Default.Clear,
                        stringResource(R.string.common_clear)
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun ToolFilterRow(
    tools: List<Tool>,
    selectedToolId: Long?,
    onFilterSelected: (Long?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedToolId == null,
                onClick = { onFilterSelected(null) },
                label = { Text(stringResource(R.string.emails_filter_all)) },
                shape = RoundedCornerShape(12.dp),
                leadingIcon = if (selectedToolId == null) {
                    { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }
                } else null
            )
        }
        items(tools, key = { it.id }) { tool ->
            FilterChip(
                selected = selectedToolId == tool.id,
                onClick = {
                    onFilterSelected(if (selectedToolId == tool.id) null else tool.id)
                },
                label = { Text(tool.name) },
                leadingIcon = {
                    Icon(Icons.Default.Build, null, Modifier.size(16.dp))
                },
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
private fun EmailsEmptyState(hasSearch: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Outlined.Email,
            null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.emails_empty_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(
                if (hasSearch) R.string.emails_empty_search_hint
                else R.string.emails_empty_add_hint
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmailsList(
    emails: List<com.ae.emailrotator.domain.model.Email>,
    onLimitClick: (com.ae.emailrotator.domain.model.Email) -> Unit,
    onUpdateLimitClick: (com.ae.emailrotator.domain.model.Email) -> Unit,
    onEditClick: (com.ae.emailrotator.domain.model.Email) -> Unit,
    onDeleteClick: (com.ae.emailrotator.domain.model.Email) -> Unit,
    onVerifyClick: (com.ae.emailrotator.domain.model.Email) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(emails, key = { "email_list_${it.id}" }) { email ->
            EmailListCard(
                email = email,
                onLimitClick = { onLimitClick(email) },
                onUpdateLimitClick = { onUpdateLimitClick(email) },
                onEditClick = { onEditClick(email) },
                onDeleteClick = { onDeleteClick(email) },
                onVerifyClick = { onVerifyClick(email) }
            )
        }
        item(key = "fab_space") {
            Spacer(Modifier.height(96.dp))
        }
    }
}
