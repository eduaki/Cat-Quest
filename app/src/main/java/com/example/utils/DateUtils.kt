package com.example.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val monthYearFormat = SimpleDateFormat("MMMM 'de' yyyy", Locale("pt", "BR"))
    private val dayMonthFormat = SimpleDateFormat("d 'de' MMMM", Locale("pt", "BR"))
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun getTodayDateString(): String {
        return isoDateFormat.format(Date())
    }

    fun getCurrentMonthPattern(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        return String.format(Locale.US, "%04d-%02d%%", year, month)
    }

    fun getMonthPattern(year: Int, month: Int): String {
        return String.format(Locale.US, "%04d-%02d%%", year, month)
    }

    fun formatMonthYear(year: Int, month: Int): String {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val str = monthYearFormat.format(cal.time)
        return str.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("pt", "BR")) else it.toString() }
    }

    fun formatDayMonth(dateStr: String): String {
        return try {
            val date = isoDateFormat.parse(dateStr) ?: return dateStr
            dayMonthFormat.format(date)
        } catch (e: Exception) {
            dateStr
        }
    }

    fun formatTime(timestampMillis: Long): String {
        return timeFormat.format(Date(timestampMillis))
    }

    fun getDaysInMonth(year: Int, month: Int): List<CalendarDay> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }

        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1=Sunday, 7=Saturday
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val days = mutableListOf<CalendarDay>()

        // Fill leading blanks (Sunday = 1, so offset = firstDayOfWeek - 1)
        for (i in 1 until firstDayOfWeek) {
            days.add(CalendarDay(dayNumber = 0, dateString = "", isCurrentMonth = false))
        }

        for (day in 1..maxDays) {
            val dateStr = String.format(Locale.US, "%04d-%02d-%02d", year, month, day)
            days.add(CalendarDay(dayNumber = day, dateString = dateStr, isCurrentMonth = true))
        }

        return days
    }

    fun calculateStreak(completedDates: Set<String>): Int {
        if (completedDates.isEmpty()) return 0
        val cal = Calendar.getInstance()
        var streak = 0

        // Check today first
        val todayStr = isoDateFormat.format(cal.time)
        val hasToday = completedDates.contains(todayStr)

        if (hasToday) {
            streak++
            cal.add(Calendar.DAY_OF_YEAR, -1)
        } else {
            // Check yesterday
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = isoDateFormat.format(cal.time)
            if (!completedDates.contains(yesterdayStr)) {
                return 0
            }
        }

        // Count backwards consecutive days
        while (true) {
            val dStr = isoDateFormat.format(cal.time)
            if (completedDates.contains(dStr)) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }

        return streak
    }

    fun getCurrentWeekDays(): List<WeekDayInfo> {
        val cal = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            val currentDayOfWeek = get(Calendar.DAY_OF_WEEK)
            val daysFromMonday = if (currentDayOfWeek == Calendar.SUNDAY) 6 else currentDayOfWeek - Calendar.MONDAY
            add(Calendar.DAY_OF_YEAR, -daysFromMonday)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val todayStr = getTodayDateString()
        val labels = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")
        val list = mutableListOf<WeekDayInfo>()

        for (i in 0 until 7) {
            val dateStr = isoDateFormat.format(cal.time)
            val dayNum = cal.get(Calendar.DAY_OF_MONTH)
            list.add(
                WeekDayInfo(
                    dateStr = dateStr,
                    dayLabel = labels[i],
                    dayNumber = dayNum,
                    isToday = dateStr == todayStr
                )
            )
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return list
    }
}

data class WeekDayInfo(
    val dateStr: String,
    val dayLabel: String,
    val dayNumber: Int,
    val isToday: Boolean
)

data class CalendarDay(
    val dayNumber: Int,
    val dateString: String,
    val isCurrentMonth: Boolean
)
