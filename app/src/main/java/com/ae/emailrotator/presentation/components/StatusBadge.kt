package com.ae.emailrotator.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.presentation.theme.StatusAvailable
import com.ae.emailrotator.presentation.theme.StatusLimited

@Composable
fun StatusBadge(status: EmailStatus) {
    val color by animateColorAsState(
        targetValue = when (status) {
            EmailStatus.AVAILABLE -> StatusAvailable
            EmailStatus.LIMITED -> StatusLimited
        },
        label = "statusColor"
    )

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = status.name,
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}