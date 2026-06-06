package com.wallet.app.util

import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val dayFormatter = SimpleDateFormat("EEEE", Locale.US)
    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    private val monthYearFormatter = SimpleDateFormat("MMMM yyyy", Locale.US)
    private val timeFormatter = SimpleDateFormat("h:mm a", Locale.US)

    fun today(): Date = Calendar.getInstance().time

    fun todayLocal(): LocalDate = LocalDate.now()

    fun formatDate(date: Date): String = dateFormatter.format(date)

    fun formatDay(date: Date): String = dayFormatter.format(date)

    fun formatMonthYear(date: Date): String = monthYearFormatter.format(date)

    fun formatTime(date: Date): String = timeFormatter.format(date)

    fun startOfMonth(): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    fun endOfMonth(): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.time
    }

    fun startOfDay(date: Date = today()): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    fun endOfDay(date: Date = today()): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.time
    }

    fun getStartOfWeek(): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        return cal.time
    }

    fun getDaysInMonth(): Int {
        val cal = Calendar.getInstance()
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun dateToLocalDate(date: Date): LocalDate {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    }

    fun localDateToDate(localDate: LocalDate): Date {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

    fun getDateRangeLabel(start: Date, end: Date): String {
        return "${formatDate(start)} - ${formatDate(end)}"
    }

    fun formatRelative(date: Date): String {
        val now = Calendar.getInstance()
        val then = Calendar.getInstance().apply { time = date }
        val diffDays = ((now.timeInMillis - then.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()

        return when {
            diffDays == 0 -> "Today"
            diffDays == 1 -> "Yesterday"
            diffDays < 7 -> "$diffDays days ago"
            diffDays < 30 -> "${diffDays / 7} weeks ago"
            diffDays < 365 -> "${diffDays / 30} months ago"
            else -> "${diffDays / 365} years ago"
        }
    }
}
