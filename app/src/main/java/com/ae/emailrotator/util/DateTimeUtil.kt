package com.ae.emailrotator.util

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtil {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("MMM dd 'at' hh:mm a", Locale.getDefault())

    fun formatDate(ts: Long): String = dateFormat.format(Date(ts))
    fun formatTime(ts: Long): String = timeFormat.format(Date(ts))
    fun formatDateTime(ts: Long): String = dateTimeFormat.format(Date(ts))

    fun formatCountdown(ts: Long): String {
        val diff = ts - System.currentTimeMillis()
        return when {
            diff <= 0 -> "Now"
            diff < 60_000L -> "< 1m"
            diff < 3_600_000L -> "${diff / 60_000}m"
            diff < 86_400_000L -> {
                val h = diff / 3_600_000
                val m = (diff % 3_600_000) / 60_000
                "${h}h ${m}m"
            }
            else -> dateTimeFormat.format(Date(ts))
        }
    }

    fun combine(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long =
        Calendar.getInstance().apply {
            set(year, month, day, hour, minute, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    fun daysFromNow(days: Int): Long =
        Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, days)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    fun calendarFromMillis(ts: Long): Calendar =
        Calendar.getInstance().apply { timeInMillis = ts }
}
