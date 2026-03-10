package com.ae.emailrotator.presentation.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ae.emailrotator.presentation.theme.*
import com.ae.emailrotator.util.DateTimeUtil
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LimitBottomSheet(
    emailAddress: String,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val cal = remember { Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, 1) } }

    var year by remember { mutableIntStateOf(cal.get(Calendar.YEAR)) }
    var month by remember { mutableIntStateOf(cal.get(Calendar.MONTH)) }
    var day by remember { mutableIntStateOf(cal.get(Calendar.DAY_OF_MONTH)) }
    var hour by remember { mutableIntStateOf(cal.get(Calendar.HOUR_OF_DAY)) }
    var minute by remember { mutableIntStateOf(cal.get(Calendar.MINUTE)) }
    var hasDate by remember { mutableStateOf(false) }
    var hasTime by remember { mutableStateOf(false) }

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
        Column(Modifier.fillMaxWidth().padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GradientBox(
                    gradient = Brush.linearGradient(listOf(Red500, Amber500)),
                    size = 44.dp
                ) {
                    Icon(Icons.Default.Block, null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Limit Email", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(emailAddress, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                "Set when this email becomes available again.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            // Date
            OutlinedCard(
                onClick = {
                    DatePickerDialog(context, { _, y, m, d -> year = y; month = m; day = d; hasDate = true },
                        year, month, day
                    ).apply { datePicker.minDate = System.currentTimeMillis() }.show()
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, null, tint = Blue500)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Date", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            if (hasDate) DateTimeUtil.formatDate(DateTimeUtil.combine(year, month, day, hour, minute))
                            else "Select a date",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (hasDate) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                    if (hasDate) Icon(Icons.Default.Check, null, tint = Green500, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(Modifier.height(12.dp))

            // Time
            OutlinedCard(
                onClick = {
                    TimePickerDialog(context, { _, h, m -> hour = h; minute = m; hasTime = true },
                        hour, minute, false
                    ).show()
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, null, tint = Purple500)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Time", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            if (hasTime) DateTimeUtil.formatTime(DateTimeUtil.combine(year, month, day, hour, minute))
                            else "Select a time",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (hasTime) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                    if (hasTime) Icon(Icons.Default.Check, null, tint = Green500, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { onConfirm(DateTimeUtil.combine(year, month, day, hour, minute)) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = hasDate && hasTime,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Red500)
            ) {
                Icon(Icons.Default.Block, null, Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Limit Email", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
