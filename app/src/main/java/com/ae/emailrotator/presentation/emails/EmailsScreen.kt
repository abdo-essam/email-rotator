package com.ae.emailrotator.presentation.emails

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ae.emailrotator.domain.model.ToolType
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

    // Bottom Sheets & Dialogs
    if (state.showAddSheet) {
        AddEditEmailSheet(
            editingEmail = state.editingEmail,
            onSave = { address, tool -> viewModel.saveEmail(address, tool) },
            onDismiss = { viewModel.dismissSheet() }
        )
    }

    state.limitEmail?.let { email ->
        LimitBottomSheet(
            emailAddress = email.address,
            onConfirm = { viewModel.limitEmail(email.id, it) },
            onDismiss = { viewModel.dismissLimit() }
        )
    }

    state.deleteEmail?.let { email ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissDelete() },
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, null, tint = Red500, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Delete Email", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text("Delete \"${email.address}\"?", style = MaterialTheme.typography.bodyMedium)
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteEmail(email.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = Red500),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Delete") }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.dismissDelete() },
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Cancel") }
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
                            "All Emails",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${state.emails.size} email(s)",
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
            LargeFloatingActionButton(
                onClick = { viewModel.showAdd() },
                shape = CircleShape,
                containerColor = Blue500,
                contentColor = Color.White
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Email",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.setSearch(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search emails...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearch("") }) {
                            Icon(Icons.Default.Clear, "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            // Tool filter
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = state.toolFilter == null,
                        onClick = { viewModel.setToolFilter(null) },
                        label = { Text("All") },
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = if (state.toolFilter == null) {
                            { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }
                        } else null
                    )
                }
                items(ToolType.entries.toList()) { tool ->
                    val icon = when (tool) {
                        ToolType.CLAUDE -> Icons.Default.AutoAwesome
                        ToolType.GEMINI -> Icons.Default.Storm
                    }
                    FilterChip(
                        selected = state.toolFilter == tool,
                        onClick = {
                            viewModel.setToolFilter(
                                if (state.toolFilter == tool) null else tool
                            )
                        },
                        label = { Text(tool.displayName) },
                        leadingIcon = {
                            Icon(icon, null, Modifier.size(16.dp))
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.emails.isEmpty()) {
                // Empty state
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
                        "No emails found",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        if (state.searchQuery.isNotBlank()) "Try a different search"
                        else "Tap + to add your first email",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        state.emails,
                        key = { "email_list_${it.id}" }
                    ) { email ->
                        EmailListCard(
                            email = email,
                            onLimitClick = { viewModel.showLimit(email) },
                            onEditClick = { viewModel.showEdit(email) },
                            onDeleteClick = { viewModel.showDelete(email) }
                        )
                    }

                    item(key = "fab_space") {
                        Spacer(Modifier.height(96.dp))
                    }
                }
            }
        }
    }
}
