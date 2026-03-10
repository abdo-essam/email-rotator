package com.ae.emailrotator.presentation.emails

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ae.emailrotator.R
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.Tool

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEmailSheet(
    email: Email?,
    tools: List<Tool>,
    onSave: (String, Long) -> Unit,
    onDismiss: () -> Unit
) {
    var address by remember { mutableStateOf(email?.address ?: "") }
    var selectedToolId by remember { mutableStateOf(email?.toolId ?: tools.firstOrNull()?.id ?: 0L) }
    var addressError by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(if (email == null) R.string.emails_add else R.string.emails_edit),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.emails_add_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            // Tool Selection
            Text(
                text = stringResource(R.string.sheet_tool_label),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                val selectedToolName = tools.find { it.id == selectedToolId }?.name
                    ?: stringResource(R.string.sheet_tool_select_label)

                OutlinedTextField(
                    value = selectedToolName,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    tools.forEach { tool ->
                        DropdownMenuItem(
                            text = { Text(tool.name) },
                            onClick = {
                                selectedToolId = tool.id
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Email Address
            Text(
                text = stringResource(R.string.sheet_email_label),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = address,
                onValueChange = {
                    address = it
                    addressError = null
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.sheet_email_placeholder)) },
                isError = addressError != null,
                supportingText = { addressError?.let { Text(it) } },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = {
                    val trimmed = address.trim()
                    if (trimmed.isBlank()) {
                        addressError = "Email is required"
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) {
                        addressError = "Invalid email format"
                    } else if (selectedToolId == 0L) {
                        addressError = "Please select a tool"
                    } else {
                        onSave(trimmed, selectedToolId)
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
                    text = stringResource(R.string.common_save),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
