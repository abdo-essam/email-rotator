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
        val diff = timestamp - System.currentTimeMillis()
        return when {
            diff <= 0 -> "Now"
            diff < 60_000 -> "Less than a minute"
            diff < 3_600_000 -> "${diff / 60_000}m left"
            diff < 86_400_000 -> "${diff / 3_600_000}h ${(diff % 3_600_000) / 60_000}m"
            else -> relativeFormat.format(Date(timestamp))
        }
    }

    fun combineDateAndTime(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
        return Calendar.getInstance().apply {
            set(year, month, day, hour, minute, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun formatTimeAgo(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        return when {
            diff < 60_000 -> "Just now"
            diff < 3_600_000 -> "${diff / 60_000}m ago"
            diff < 86_400_000 -> "${diff / 3_600_000}h ago"
            diff < 604_800_000 -> "${diff / 86_400_000}d ago"
            else -> dateFormat.format(Date(timestamp))
        }
    }
}
