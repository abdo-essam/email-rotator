package com.ae.emailrotator.presentation.emails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ae.emailrotator.R
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEmailSheet(
    editingEmail: Email?,
    onSave: (address: String, needsVerification: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var address by remember(editingEmail) {
        mutableStateOf(editingEmail?.address ?: "")
    }
    var needsVerification by remember(editingEmail) {
        mutableStateOf(editingEmail?.status == EmailStatus.NEEDS_VERIFICATION)
    }
    var addressError by remember { mutableStateOf<String?>(null) }

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
                text = stringResource(
                    if (editingEmail != null) R.string.emails_edit else R.string.emails_add
                ),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (editingEmail != null)
                    "Update the email address"
                else
                    "Email will be added to all tools automatically",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it; addressError = null },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.sheet_email_label)) },
                placeholder = { Text(stringResource(R.string.sheet_email_placeholder)) },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                isError = addressError != null,
                supportingText = addressError?.let {
                    { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(Modifier.height(16.dp))

            VerificationToggleRow(
                checked = needsVerification,
                onCheckedChange = { needsVerification = it }
            )

            Spacer(Modifier.height(28.dp))

            val requiredError = stringResource(R.string.sheet_email_required)
            val invalidError = stringResource(R.string.sheet_email_invalid)

            Button(
                onClick = {
                    val trimmed = address.trim()
                    when {
                        trimmed.isBlank() ->
                            addressError = requiredError
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmed).matches() ->
                            addressError = invalidError
                        else -> onSave(trimmed, needsVerification)
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
                    text = stringResource(
                        if (editingEmail != null) R.string.action_update_email
                        else R.string.action_add_email
                    ),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun VerificationToggleRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.HelpOutline,
            contentDescription = null,
            tint = Amber500,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.status_needs_verification),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Email won't be used until verified",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Amber500,
                checkedTrackColor = Amber500.copy(alpha = 0.3f)
            )
        )
    }
}
