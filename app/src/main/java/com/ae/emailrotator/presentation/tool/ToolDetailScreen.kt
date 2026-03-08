package com.ae.emailrotator.presentation.tool

import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.presentation.components.AnimatedStatusChip
import com.ae.emailrotator.presentation.components.EmptyStateIllustration
import com.ae.emailrotator.presentation.components.GlassCard
import com.ae.emailrotator.presentation.components.GradientBadge
import com.ae.emailrotator.presentation.components.GradientIconBox
import com.ae.emailrotator.presentation.components.LimitEmailBottomSheet
import com.ae.emailrotator.presentation.components.PulsingDot
import com.ae.emailrotator.presentation.navigation.NavRoutes
import com.ae.emailrotator.presentation.theme.AccentPurple
import com.ae.emailrotator.presentation.theme.Gradients
import com.ae.emailrotator.presentation.theme.PrimaryBlue
import com.ae.emailrotator.presentation.theme.StatusAmber
import com.ae.emailrotator.presentation.theme.StatusGreen
import com.ae.emailrotator.presentation.theme.StatusRed
import com.ae.emailrotator.util.DateTimeUtil
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolDetailScreen(
    navController: NavController,
    toolId: Long,
    viewModel: ToolDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toolId) { viewModel.loadTool(toolId) }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    uiState.limitDialogEmail?.let { eit ->
        LimitEmailBottomSheet(
            emailAddress = eit.email.address,
            onConfirm = { viewModel.limitEmail(eit.email.id, it) },
            onDismiss = { viewModel.dismissLimitDialog() }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    val twe = uiState.toolWithEmails
                    if (twe != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GradientIconBox(
                                gradient = Brush.linearGradient(listOf(AccentPurple, PrimaryBlue)),
                                size = 36
                            ) {
                                Icon(Icons.Default.Extension, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    twe.tool.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                val active = twe.currentActiveEmail
                                if (active != null) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        PulsingDot(StatusGreen, 6)
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            active.address,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Text("Tool Details")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    uiState.toolWithEmails?.let { twe ->
                        IconButton(onClick = { navController.navigate(NavRoutes.editTool(twe.tool.id)) }) {
                            Icon(Icons.Outlined.Edit, "Edit")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(NavRoutes.addEmailForTool(toolId)) },
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Add Email") }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val twe = uiState.toolWithEmails
            if (twe == null) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Tool not found")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Rotation Status Card
                    item {
                        GlassCard(Modifier.fillMaxWidth()) {
                            Text("Rotation Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(12.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "${twe.emails.size}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBlue
                                    )
                                    Text("Total", style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "${twe.availableCount}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusGreen
                                    )
                                    Text("Available", style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "${twe.limitedCount}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusRed
                                    )
                                    Text("Limited", style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    // Email Rotation Order
                    item {
                        Text(
                            "Email Rotation Order",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Text(
                            "Emails are rotated in this order when limited",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (twe.emails.isEmpty()) {
                        item {
                            EmptyStateIllustration(
                                icon = Icons.Default.Email,
                                title = "No emails assigned",
                                subtitle = "Add emails to start rotation"
                            )
                        }
                    } else {
                        val sorted = twe.emails.sortedBy { it.orderIndex }
                        itemsIndexed(sorted, key = { _, eit -> "rot_email_${eit.email.id}" }) { index, eit ->
                            val isActive = twe.currentActiveEmail?.id == eit.email.id

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isActive)
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                    else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                border = if (isActive) CardDefaults.outlinedCardBorder().copy(
                                    brush = Brush.linearGradient(listOf(PrimaryBlue, AccentPurple))
                                ) else null
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Order number
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isActive) PrimaryBlue
                                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "${index + 1}",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isActive) Color.White
                                            else MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Spacer(Modifier.width(12.dp))

                                    Column(Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                eit.email.address,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (isActive) {
                                                Spacer(Modifier.width(8.dp))
                                                GradientBadge("ACTIVE", Gradients.greenGradient)
                                            }
                                        }
                                        if (eit.email.status == EmailStatus.LIMITED && eit.email.availableAt != null) {
                                            Text(
                                                "Available: ${DateTimeUtil.formatRelative(eit.email.availableAt)}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = StatusAmber
                                            )
                                        }
                                    }

                                    AnimatedStatusChip(eit.email.status)

                                    if (eit.email.status == EmailStatus.AVAILABLE && !isActive) {
                                        IconButton(
                                            onClick = { viewModel.showLimitDialog(eit) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Outlined.Block, null,
                                                Modifier.size(16.dp), tint = StatusAmber
                                            )
                                        }
                                    } else if (eit.email.status == EmailStatus.AVAILABLE && isActive) {
                                        IconButton(
                                            onClick = { viewModel.showLimitDialog(eit) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Outlined.Block, null,
                                                Modifier.size(16.dp), tint = StatusAmber
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}