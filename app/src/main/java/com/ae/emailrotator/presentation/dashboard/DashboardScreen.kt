package com.ae.emailrotator.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.presentation.components.*
import com.ae.emailrotator.presentation.theme.*
import com.ae.emailrotator.util.DateTimeUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(state.snackbar) {
        state.snackbar?.let {
            snackbarHost.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    state.limitEmail?.let { email ->
        LimitBottomSheet(
            emailAddress = email.address,
            onConfirm = { viewModel.limitEmail(email.id, it) },
            onDismiss = { viewModel.dismissLimit() }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHost) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Email Rotator",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Pick your next available email",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onToggleDarkMode) {
                        Icon(
                            if (isDarkMode) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                            contentDescription = "Toggle theme"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ===== Claude Section =====
                item(key = "claude_header") {
                    ToolSectionHeader(
                        toolName = "Claude",
                        icon = Icons.Default.AutoAwesome,
                        gradient = Brush.linearGradient(listOf(Blue500, Purple500))
                    )
                }

                item(key = "claude_current") {
                    CurrentEmailCard(
                        email = state.claudeCurrent,
                        toolName = "Claude — Current Email",
                        queueSize = state.claudeQueue.size
                    )
                }

                if (state.claudeCurrent != null) {
                    item(key = "claude_limit_btn") {
                        LimitCurrentButton(
                            email = state.claudeCurrent!!,
                            onLimitClick = { viewModel.showLimit(state.claudeCurrent!!) }
                        )
                    }
                }

                if (state.claudeQueue.size > 1) {
                    item(key = "claude_queue_label") {
                        Text(
                            "Queue",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                    items(
                        state.claudeQueue.drop(1),
                        key = { "claude_q_${it.id}" }
                    ) { email ->
                        QueueEmailItem(email = email)
                    }
                }

                // ===== Spacer =====
                item(key = "divider") {
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    Spacer(Modifier.height(8.dp))
                }

                // ===== Gemini Section =====
                item(key = "gemini_header") {
                    ToolSectionHeader(
                        toolName = "Gemini",
                        icon = Icons.Default.Storm,
                        gradient = Brush.linearGradient(listOf(Teal500, Blue500))
                    )
                }

                item(key = "gemini_current") {
                    CurrentEmailCard(
                        email = state.geminiCurrent,
                        toolName = "Gemini — Current Email",
                        queueSize = state.geminiQueue.size
                    )
                }

                if (state.geminiCurrent != null) {
                    item(key = "gemini_limit_btn") {
                        LimitCurrentButton(
                            email = state.geminiCurrent!!,
                            onLimitClick = { viewModel.showLimit(state.geminiCurrent!!) }
                        )
                    }
                }

                if (state.geminiQueue.size > 1) {
                    item(key = "gemini_queue_label") {
                        Text(
                            "Queue",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                    items(
                        state.geminiQueue.drop(1),
                        key = { "gemini_q_${it.id}" }
                    ) { email ->
                        QueueEmailItem(email = email)
                    }
                }

                item(key = "bottom_space") {
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun ToolSectionHeader(
    toolName: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: Brush
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        GradientBox(gradient = gradient, size = 36.dp, cornerRadius = 10.dp) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(
            toolName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LimitCurrentButton(email: Email, onLimitClick: () -> Unit) {
    Button(
        onClick = onLimitClick,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Amber500.copy(alpha = 0.15f),
            contentColor = Amber500
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Icon(Icons.Outlined.Block, null, Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            "Limit Current Email",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun QueueEmailItem(email: Email) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusDot(email.status)
            Spacer(Modifier.width(12.dp))
            Text(
                email.address,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                "Ready",
                style = MaterialTheme.typography.labelSmall,
                color = Green500,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}