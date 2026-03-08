package com.ae.emailrotator.presentation.dashboard.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.domain.model.EmailStatusRow
import com.ae.emailrotator.presentation.components.StatusBadge
import com.ae.emailrotator.presentation.theme.StatusLimited
import com.ae.emailrotator.util.DateTimeUtil
import kotlin.text.ifEmpty

@Composable
fun EmailStatusRowCard(
    emailRow: EmailStatusRow,
    onLimitClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1.5f)) {
                    Text(
                        text = emailRow.emailAddress,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (emailRow.status == EmailStatus.LIMITED && emailRow.availableAt != null) {
                        Text(
                            text = DateTimeUtil.formatRelative(emailRow.availableAt),
                            style = MaterialTheme.typography.labelSmall,
                            color = StatusLimited.copy(alpha = 0.8f)
                        )
                    }
                }

                Text(
                    text = emailRow.toolNames.joinToString(", ").ifEmpty { "-" },
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Box(modifier = Modifier.weight(0.8f)) {
                    StatusBadge(status = emailRow.status)
                }

                Box(modifier = Modifier.weight(0.7f)) {
                    if (emailRow.status == EmailStatus.AVAILABLE) {
                        TextButton(
                            onClick = onLimitClick,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text(
                                "Limit",
                                style = MaterialTheme.typography.labelMedium,
                                color = StatusLimited
                            )
                        }
                    } else {
                        Text(
                            "—",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                }
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                thickness = 0.5.dp
            )
        }
    }
}