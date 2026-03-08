package com.ae.emailrotator.presentation.components

import com.ae.emailrotator.presentation.theme.Gradients
import com.ae.emailrotator.presentation.theme.PrimaryBlue
import com.ae.emailrotator.presentation.theme.StatusGreen
import com.ae.emailrotator.util.DateTimeUtil
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ae.emailrotator.presentation.theme.AccentPurple
import com.ae.emailrotator.presentation.theme.StatusRed
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LimitEmailBottomSheet(
    emailAddress: String,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, 1) } }

    var selectedYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableIntStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var selectedHour by remember { mutableIntStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableIntStateOf(calendar.get(Calendar.MINUTE)) }
    var dateSelected by remember { mutableStateOf(false) }
    var timeSelected by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
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
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                GradientIconBox(gradient = Gradients.redGradient, size = 44) {
                    Icon(
                        Icons.Default.Block,
                        contentDescription = null,
                        tint = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        "Limit Email",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        emailAddress,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                "Set when this email will become available again.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            // Date picker
            OutlinedCard(
                onClick = {
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            selectedYear = year
                            selectedMonth = month
                            selectedDay = day
                            dateSelected = true
                        },
                        selectedYear, selectedMonth, selectedDay
                    ).apply { datePicker.minDate = System.currentTimeMillis() }.show()
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Date", style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            if (dateSelected) DateTimeUtil.formatDate(
                                DateTimeUtil.combineDateAndTime(
                                    selectedYear, selectedMonth, selectedDay,
                                    selectedHour, selectedMinute
                                )
                            ) else "Select a date",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (dateSelected) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                    if (dateSelected) {
                        Icon(Icons.Default.Check, null, tint = StatusGreen, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Time picker
            OutlinedCard(
                onClick = {
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            selectedHour = hour
                            selectedMinute = minute
                            timeSelected = true
                        },
                        selectedHour, selectedMinute, false
                    ).show()
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = AccentPurple,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Time", style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            if (timeSelected) DateTimeUtil.formatTime(
                                DateTimeUtil.combineDateAndTime(
                                    selectedYear, selectedMonth, selectedDay,
                                    selectedHour, selectedMinute
                                )
                            ) else "Select a time",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (timeSelected) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                    if (timeSelected) {
                        Icon(Icons.Default.Check, null, tint = StatusGreen, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    onConfirm(
                        DateTimeUtil.combineDateAndTime(
                            selectedYear, selectedMonth, selectedDay,
                            selectedHour, selectedMinute
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = dateSelected && timeSelected,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StatusRed)
            ) {
                Icon(Icons.Default.Block, null, Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Limit Email", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}