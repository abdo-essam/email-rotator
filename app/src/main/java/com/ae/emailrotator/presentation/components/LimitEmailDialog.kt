package com.ae.emailrotator.presentation.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ae.emailrotator.util.DateTimeUtil
import java.util.*

@Composable
fun LimitEmailDialog(
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Limit Email") },
        text = {
            Column {
                Text(
                    text = "Set when $emailAddress will become available again.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                selectedYear = year
                                selectedMonth = month
                                selectedDay = day
                                dateSelected = true
                            },
                            selectedYear,
                            selectedMonth,
                            selectedDay
                        ).apply {
                            datePicker.minDate = System.currentTimeMillis()
                        }.show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (dateSelected) DateTimeUtil.formatDate(
                            DateTimeUtil.combineDateAndTime(
                                selectedYear, selectedMonth, selectedDay,
                                selectedHour, selectedMinute
                            )
                        ) else "Select Date"
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                selectedHour = hour
                                selectedMinute = minute
                                timeSelected = true
                            },
                            selectedHour,
                            selectedMinute,
                            false
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (timeSelected) DateTimeUtil.formatTime(
                            DateTimeUtil.combineDateAndTime(
                                selectedYear, selectedMonth, selectedDay,
                                selectedHour, selectedMinute
                            )
                        ) else "Select Time"
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val timestamp = DateTimeUtil.combineDateAndTime(
                        selectedYear, selectedMonth, selectedDay,
                        selectedHour, selectedMinute
                    )
                    onConfirm(timestamp)
                },
                enabled = dateSelected && timeSelected
            ) {
                Text("Limit Email")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
