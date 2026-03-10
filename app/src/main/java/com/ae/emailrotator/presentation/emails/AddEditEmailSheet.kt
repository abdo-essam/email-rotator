package com.ae.emailrotator.presentation.emails

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.ToolType
import com.ae.emailrotator.presentation.components.GradientBox
import com.ae.emailrotator.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEmailSheet(
    editingEmail: Email?,
    onSave: (String, ToolType) -> Unit,
    onDismiss: () -> Unit
) {
    var address by remember(editingEmail) { mutableStateOf(editingEmail?.address ?: "") }
    var tool by remember(editingEmail) { mutableStateOf(editingEmail?.tool ?: ToolType.CLAUDE) }
    var error by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                Modifier
                    .padding(top = 12.dp)
                    .size(40.dp, 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                if (editingEmail != null) "Edit Email" else "Add Email",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Add an email and assign it to a tool",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            // Tool Selection
            Text(
                "Tool",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ToolType.entries.forEach { t ->
                    val selected = tool == t
                    val gradient = when (t) {
                        ToolType.CLAUDE -> Brush.linearGradient(listOf(Blue500, Purple500))
                        ToolType.GEMINI -> Brush.linearGradient(listOf(Teal500, Blue500))
                    }
                    val icon = when (t) {
                        ToolType.CLAUDE -> Icons.Default.AutoAwesome
                        ToolType.GEMINI -> Icons.Default.Storm
                    }
                    val borderColor = if (selected) Blue500 else MaterialTheme.colorScheme.outline

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
                            .clickable { tool = t },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected)
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            GradientBox(gradient = gradient, size = 44.dp) {
                                Icon(icon, null, tint = Color.White, modifier = Modifier.size(22.dp))
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                t.displayName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Email Address
            OutlinedTextField(
                value = address,
                onValueChange = { address = it; error = null },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email Address") },
                placeholder = { Text("user@example.com") },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                isError = error != null,
                supportingText = error?.let {
                    { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = {
                    val trimmed = address.trim()
                    when {
                        trimmed.isBlank() -> error = "Email is required"
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmed).matches() ->
                            error = "Invalid email format"
                        else -> onSave(trimmed, tool)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Check, null, Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    if (editingEmail != null) "Update Email" else "Add Email",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
