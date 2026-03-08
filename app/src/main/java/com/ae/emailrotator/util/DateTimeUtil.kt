package com.ae.emailrotator.util

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtil {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
    private val relativeFormat = SimpleDateFormat("MMM dd 'at' hh:mm a", Locale.getDefault())

    fun formatDate(timestamp: Long): String = dateFormat.format(Date(timestamp))

    fun formatTime(timestamp: Long): String = timeFormat.format(Date(timestamp))

    fun formatDateTime(timestamp: Long): String = dateTimeFormat.format(Date(timestamp))

    fun formatRelative(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = timestamp - now

        return when {
            diff <= 0 -> "Now"
            diff < 60_000 -> "Less than a minute"
            diff < 3_600_000 -> "${diff / 60_000} min"
            diff < 86_400_000 -> "${diff / 3_600_000}h ${(diff % 3_600_000) / 60_000}m"
            else -> relativeFormat.format(Date(timestamp))
        }
    }

    fun isExpired(timestamp: Long?): Boolean {
        if (timestamp == null) return false
        return timestamp <= System.currentTimeMillis()
    }

    fun combineDateAndTime(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
}
