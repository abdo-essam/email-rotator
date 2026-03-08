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
import com.ae.emailrotator.presentation.components.StatusBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditToolScreen(
    navController: NavController,
    toolId: Long?,
    viewModel: AddEditToolViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(toolId) {
        viewModel.initialize(toolId)
    }

    LaunchedEffect(uiState.savedSuccessfully) {
        if (uiState.savedSuccessfully) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (uiState.isEditing) "Edit Tool" else "Add Tool")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
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
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = uiState.toolName,
                        onValueChange = { viewModel.updateToolName(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Tool Name") },
                        leadingIcon = {
                            Icon(Icons.Default.Build, contentDescription = null)
                        },
                        isError = uiState.nameError != null,
                        supportingText = uiState.nameError?.let {
                            { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium
                    )
                }

                item {
                    Text(
                        text = "Assign Emails",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                if (uiState.allEmails.isEmpty()) {
                    item {
                        Text(
                            text = "No emails available. Create an email first.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                } else {
                    items(uiState.allEmails) { email ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = email.address,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    StatusBadge(status = email.status)
                                }
                                Checkbox(
                                    checked = email.id in uiState.selectedEmailIds,
                                    onCheckedChange = { viewModel.toggleEmail(email.id) }
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.save() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !uiState.isSaving,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (uiState.isEditing) "Update Tool" else "Add Tool")
                        }
                    }
                }
            }
        }
    }
}
