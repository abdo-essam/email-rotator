package com.ae.emailrotator.util

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtil {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("MMM dd 'at' hh:mm a", Locale.getDefault())
    private val shortDateFormat = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
    private val fullDateTimeFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())

    fun formatDate(ts: Long): String = dateFormat.format(Date(ts))
    fun formatTime(ts: Long): String = timeFormat.format(Date(ts))
    fun formatDateTime(ts: Long): String = dateTimeFormat.format(Date(ts))
    fun formatShortDate(ts: Long): String = shortDateFormat.format(Date(ts))
    fun formatFullDateTime(ts: Long): String = fullDateTimeFormat.format(Date(ts))

    /**
     * Formats countdown with full precision:
     * - "< 1m" for less than a minute
     * - "45m" for minutes only
     * - "2h 30m" for hours and minutes
     * - "1d 14h 23m" for days, hours, and minutes
     * - "5d 2h" for longer durations (omit minutes if > 1 day)
     */
    fun formatCountdown(ts: Long): String {
        val diff = ts - System.currentTimeMillis()
        if (diff <= 0) return "Now"

        val totalMinutes = diff / 60_000
        val totalHours = diff / 3_600_000
        val totalDays = diff / 86_400_000

        val days = totalDays
        val hours = (diff % 86_400_000) / 3_600_000
        val minutes = (diff % 3_600_000) / 60_000

        return when {
            diff < 60_000 -> "< 1m"
            totalDays == 0L && totalHours == 0L -> "${minutes}m"
            totalDays == 0L -> {
                if (minutes > 0) "${hours}h ${minutes}m" else "${hours}h"
            }
            totalDays < 7 -> {
                // Show days + hours + minutes for better precision
                buildString {
                    append("${days}d")
                    if (hours > 0) append(" ${hours}h")
                    if (minutes > 0 && days < 2) append(" ${minutes}m") // Only show minutes for < 2 days
                }
            }
            else -> {
                // For longer periods, show days + hours
                buildString {
                    append("${days}d")
                    if (hours > 0) append(" ${hours}h")
                }
            }
        }
    }

    /**
     * Formats countdown with date context:
     * - "in 45m" / "in 2h 30m" / "in 1d 14h 23m"
     * - Also shows the actual date/time
     */
    fun formatCountdownWithDate(ts: Long): Pair<String, String> {
        val countdown = formatCountdown(ts)
        val dateTime = when {
            isToday(ts) -> "Today at ${formatTime(ts)}"
            isTomorrow(ts) -> "Tomorrow at ${formatTime(ts)}"
            else -> formatDateTime(ts)
        }
        return Pair(countdown, dateTime)
    }

    /**
     * Short countdown for chips/badges
     */
    fun formatShortCountdown(ts: Long): String {
        val diff = ts - System.currentTimeMillis()
        if (diff <= 0) return "Now"

        val totalDays = diff / 86_400_000
        val hours = (diff % 86_400_000) / 3_600_000
        val minutes = (diff % 3_600_000) / 60_000

        return when {
            diff < 60_000 -> "<1m"
            totalDays == 0L && hours == 0L -> "${minutes}m"
            totalDays == 0L -> "${hours}h${if (minutes > 0) " ${minutes}m" else ""}"
            else -> "${totalDays}d${if (hours > 0) " ${hours}h" else ""}"
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

    fun hoursFromNow(hours: Int): Long =
        System.currentTimeMillis() + (hours * 60L * 60L * 1000L)

    fun minutesFromNow(minutes: Int): Long =
        System.currentTimeMillis() + (minutes * 60L * 1000L)

    fun calendarFromMillis(ts: Long): Calendar =
        Calendar.getInstance().apply { timeInMillis = ts }

    fun getStartOfDay(ts: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = ts
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    fun getEndOfDay(ts: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = ts
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

    fun isToday(ts: Long): Boolean {
        val today = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = ts }
        return today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
    }

    fun isTomorrow(ts: Long): Boolean {
        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }
        val date = Calendar.getInstance().apply { timeInMillis = ts }
        return tomorrow.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                tomorrow.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
    }

    fun isSameDay(ts1: Long, ts2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = ts1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = ts2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }


    fun getRelativeDay(ts: Long): String = when {
        isToday(ts) -> "Today"
        isTomorrow(ts) -> "Tomorrow"
        else -> formatShortDate(ts)
    }

    /**
     * Get components from timestamp for display
     */
    fun getComponents(ts: Long): TimeComponents {
        val diff = ts - System.currentTimeMillis()
        if (diff <= 0) return TimeComponents(0, 0, 0, 0)

        val days = (diff / 86_400_000).toInt()
        val hours = ((diff % 86_400_000) / 3_600_000).toInt()
        val minutes = ((diff % 3_600_000) / 60_000).toInt()
        val seconds = ((diff % 60_000) / 1000).toInt()

        return TimeComponents(days, hours, minutes, seconds)
    }

    data class TimeComponents(
        val days: Int,
        val hours: Int,
        val minutes: Int,
        val seconds: Int
    ) {
        fun toFormattedString(): String = buildString {
            if (days > 0) append("${days}d ")
            if (hours > 0) append("${hours}h ")
            if (minutes > 0 && days < 2) append("${minutes}m")
            if (isEmpty() || (days == 0 && hours == 0 && minutes == 0)) append("< 1m")
        }.trim()
    }
}
