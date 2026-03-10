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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ae.emailrotator.R
import com.ae.emailrotator.presentation.theme.*
import com.ae.emailrotator.util.DateTimeUtil
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LimitBottomSheet(
    emailAddress: String,
    defaultLimitDays: Int = 7,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val defaultCal = remember(defaultLimitDays) {
        Calendar.getInstance().apply {
            timeInMillis = DateTimeUtil.daysFromNow(defaultLimitDays)
        }
    }

    var year by remember { mutableIntStateOf(defaultCal.get(Calendar.YEAR)) }
    var month by remember { mutableIntStateOf(defaultCal.get(Calendar.MONTH)) }
    var day by remember { mutableIntStateOf(defaultCal.get(Calendar.DAY_OF_MONTH)) }
    var hour by remember { mutableIntStateOf(defaultCal.get(Calendar.HOUR_OF_DAY)) }
    var minute by remember { mutableIntStateOf(defaultCal.get(Calendar.MINUTE)) }

    // Pre-populated with defaults
    var hasDate by remember { mutableStateOf(true) }
    var hasTime by remember { mutableStateOf(true) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDragHandle() }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            LimitSheetHeader(emailAddress = emailAddress)

            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.limit_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            DatePickerCard(
                hasDate = hasDate,
                year = year, month = month, day = day, hour = hour, minute = minute,
                onDatePicked = { y, m, d ->
                    year = y; month = m; day = d; hasDate = true
                }
            )

            Spacer(Modifier.height(12.dp))

            TimePickerCard(
                hasTime = hasTime,
                year = year, month = month, day = day, hour = hour, minute = minute,
                onTimePicked = { h, m ->
                    hour = h; minute = m; hasTime = true
                }
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    onConfirm(DateTimeUtil.combine(year, month, day, hour, minute))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = hasDate && hasTime,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Red500)
            ) {
                Icon(Icons.Default.Block, null, Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.limit_confirm),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BottomSheetDragHandle() {
    Box(
        Modifier
            .padding(top = 12.dp)
            .size(40.dp, 4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    )
}

@Composable
private fun LimitSheetHeader(emailAddress: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        GradientBox(
            gradient = Brush.linearGradient(listOf(Red500, Amber500)),
            size = 44.dp
        ) {
            Icon(
                Icons.Default.Block,
                null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = stringResource(R.string.limit_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = emailAddress,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DatePickerCard(
    hasDate: Boolean,
    year: Int, month: Int, day: Int, hour: Int, minute: Int,
    onDatePicked: (Int, Int, Int) -> Unit
) {
    val context = LocalContext.current
    OutlinedCard(
        onClick = {
            DatePickerDialog(
                context,
                { _, y, m, d -> onDatePicked(y, m, d) },
                year, month, day
            ).apply {
                datePicker.minDate = System.currentTimeMillis()
            }.show()
        },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CalendarMonth, null, tint = Blue500)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.limit_date_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (hasDate)
                        DateTimeUtil.formatDate(DateTimeUtil.combine(year, month, day, hour, minute))
                    else
                        stringResource(R.string.limit_select_date),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (hasDate) FontWeight.Medium else FontWeight.Normal
                )
            }
            if (hasDate) {
                Icon(
                    Icons.Default.Check,
                    null,
                    tint = Green500,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun TimePickerCard(
    hasTime: Boolean,
    year: Int, month: Int, day: Int, hour: Int, minute: Int,
    onTimePicked: (Int, Int) -> Unit
) {
    val context = LocalContext.current
    OutlinedCard(
        onClick = {
            TimePickerDialog(
                context,
                { _, h, m -> onTimePicked(h, m) },
                hour, minute, false
            ).show()
        },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Schedule, null, tint = Purple500)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.limit_time_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (hasTime)
                        DateTimeUtil.formatTime(DateTimeUtil.combine(year, month, day, hour, minute))
                    else
                        stringResource(R.string.limit_select_time),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (hasTime) FontWeight.Medium else FontWeight.Normal
                )
            }
            if (hasTime) {
                Icon(
                    Icons.Default.Check,
                    null,
                    tint = Green500,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
