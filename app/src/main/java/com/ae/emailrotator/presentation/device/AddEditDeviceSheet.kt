package com.ae.emailrotator.presentation.device

import com.ae.emailrotator.domain.model.Device
import com.ae.emailrotator.domain.model.DeviceType
import com.ae.emailrotator.presentation.components.GradientIconBox
import com.ae.emailrotator.presentation.theme.Gradients
import com.ae.emailrotator.presentation.theme.PrimaryBlue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditDeviceSheet(
    editingDevice: Device?,
    onSave: (String, DeviceType) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember(editingDevice) { mutableStateOf(editingDevice?.name ?: "") }
    var type by remember(editingDevice) { mutableStateOf(editingDevice?.type ?: DeviceType.MAC) }
    var nameError by remember { mutableStateOf<String?>(null) }

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
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Text(
                if (editingDevice != null) "Edit Device" else "Add Device",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Configure a Mac or Windows device",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            // Device Type Selector
            Text("Device Type", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DeviceType.entries.forEach { deviceType ->
                    val selected = type == deviceType
                    val gradient = when (deviceType) {
                        DeviceType.MAC -> Gradients.macGradient
                        DeviceType.WINDOWS -> Gradients.windowsGradient
                    }
                    val borderColor = if (selected) PrimaryBlue else MaterialTheme.colorScheme.outline

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
                            .clickable { type = deviceType },
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
                            GradientIconBox(gradient = gradient, size = 48) {
                                Text(deviceType.icon, fontSize = 22.sp)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                deviceType.displayName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = null },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Device Name") },
                placeholder = { Text("e.g. Work MacBook Pro") },
                leadingIcon = { Icon(Icons.Default.DevicesOther, null) },
                isError = nameError != null,
                supportingText = nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = "Device name is required"
                    } else {
                        onSave(name.trim(), type)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Check, null, Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    if (editingDevice != null) "Update Device" else "Add Device",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}