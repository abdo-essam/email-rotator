package com.ae.emailrotator.presentation.tools

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ae.emailrotator.R
import com.ae.emailrotator.domain.model.Tool
import com.ae.emailrotator.presentation.components.GlassCard
import com.ae.emailrotator.presentation.components.GradientBox
import com.ae.emailrotator.presentation.theme.Blue500
import com.ae.emailrotator.presentation.theme.Purple500
import com.ae.emailrotator.presentation.theme.Red500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(
    onNavigateBack: (() -> Unit)? = null,
    viewModel: ToolsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    var showAddSheet by remember { mutableStateOf(false) }
    var toolToEdit by remember { mutableStateOf<Tool?>(null) }
    var toolToDelete by remember { mutableStateOf<Tool?>(null) }

    LaunchedEffect(state.snackbar) {
        state.snackbar?.let {
            snackbarHost.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    if (showAddSheet || toolToEdit != null) {
        AddEditToolSheet(
            tool = toolToEdit,
            onSave = { name ->
                viewModel.saveTool(name, toolToEdit?.id ?: 0L)
                showAddSheet = false
                toolToEdit = null
            },
            onDismiss = {
                showAddSheet = false
                toolToEdit = null
            }
        )
    }

    if (toolToDelete != null) {
        AlertDialog(
            onDismissRequest = { toolToDelete = null },
            title = { Text(stringResource(R.string.delete_title)) },
            text = { Text(stringResource(R.string.tools_delete_message, toolToDelete!!.name)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTool(toolToDelete!!.id)
                        toolToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Red500)
                ) {
                    Text(stringResource(R.string.delete_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { toolToDelete = null }) {
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
                            text = stringResource(R.string.tools_title),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.tools_subtitle, state.tools.size),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    if (onNavigateBack != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
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
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Tool")
            }
        }
    ) { padding ->
        if (state.tools.isEmpty() && !state.isLoading) {
            EmptyToolsState()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = state.tools, key = { it.id }) { tool ->
                    ToolItem(
                        tool = tool,
                        onEdit = { toolToEdit = tool },
                        onDelete = { toolToDelete = tool }
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolItem(
    tool: Tool,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GradientBox(
                gradient = Brush.linearGradient(listOf(Blue500, Purple500)),
                size = 40.dp
            ) {
                Icon(Icons.Default.Build, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = tool.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Outlined.Edit, null, tint = Blue500)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Outlined.Delete, null, tint = Red500)
                }
            }
        }
    }
}

@Composable
private fun EmptyToolsState() {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.tools_empty_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.tools_empty_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
