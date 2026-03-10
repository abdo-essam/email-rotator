package com.ae.emailrotator.presentation.emails

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
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
import com.ae.emailrotator.presentation.components.EmailListCard
import com.ae.emailrotator.presentation.components.LimitBottomSheet
import com.ae.emailrotator.presentation.theme.Blue500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailsScreen(
    viewModel: EmailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    var showAddSheet by remember { mutableStateOf(false) }
    var emailToEdit by remember { mutableStateOf<Email?>(null) }
    var emailToLimit by remember { mutableStateOf<Email?>(null) }
    var emailToDelete by remember { mutableStateOf<Email?>(null) }

    LaunchedEffect(state.snackbar) {
        state.snackbar?.let {
            snackbarHost.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    if (showAddSheet || emailToEdit != null) {
        AddEditEmailSheet(
            email = emailToEdit,
            tools = state.tools,
            onSave = { address, toolId ->
                viewModel.saveEmail(address, toolId, emailToEdit?.id ?: 0L)
                showAddSheet = false
                emailToEdit = null
            },
            onDismiss = {
                showAddSheet = false
                emailToEdit = null
            }
        )
    }

    emailToLimit?.let { email ->
        LimitBottomSheet(
            emailAddress = email.address,
            onConfirm = {
                viewModel.limitEmail(email.id, it)
                emailToLimit = null
            },
            onDismiss = { emailToLimit = null }
        )
    }

    if (emailToDelete != null) {
        DeleteConfirmDialog(
            emailAddress = emailToDelete!!.address,
            onConfirm = {
                viewModel.deleteEmail(emailToDelete!!.id)
                emailToDelete = null
            },
            onDismiss = { emailToDelete = null }
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
                            style = MaterialTheme.typography.labelMedium,
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
                onClick = { showAddSheet = true },
                containerColor = Blue500,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.emails_add))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SearchBar(
                query = state.searchQuery,
                onQueryChange = viewModel::onSearchChange
            )

            ToolFilterRow(
                tools = state.tools,
                selectedToolId = state.filterToolId,
                onToolSelect = viewModel::onFilterToolChange
            )

            if (state.emails.isEmpty() && !state.isLoading) {
                EmptyState(
                    isSearch = state.searchQuery.isNotEmpty() || state.filterToolId != null
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = state.emails, key = { it.id }) { email ->
                        EmailListCard(
                            email = email,
                            onLimitClick = { emailToLimit = email },
                            onEditClick = { emailToEdit = email },
                            onDeleteClick = { emailToDelete = email },
                            onVerifyClick = { viewModel.verifyEmail(email.id) }
                        )
                    }
                }
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
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    )
}

@Composable
private fun ToolFilterRow(
    tools: List<com.ae.emailrotator.domain.model.Tool>,
    selectedToolId: Long?,
    onToolSelect: (Long?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item {
            FilterChip(
                selected = selectedToolId == null,
                onClick = { onToolSelect(null) },
                label = { Text(stringResource(R.string.emails_filter_all)) }
            )
        }
        items(tools) { tool ->
            FilterChip(
                selected = selectedToolId == tool.id,
                onClick = { onToolSelect(tool.id) },
                label = { Text(tool.name) }
            )
        }
    }
}

@Composable
private fun EmptyState(isSearch: Boolean) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.emails_empty_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(
                    if (isSearch) R.string.emails_empty_search_hint
                    else R.string.emails_empty_add_hint
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DeleteConfirmDialog(
    emailAddress: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_title)) },
        text = { Text(stringResource(R.string.delete_message, emailAddress)) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(R.string.delete_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.delete_cancel))
            }
        }
    )
}
