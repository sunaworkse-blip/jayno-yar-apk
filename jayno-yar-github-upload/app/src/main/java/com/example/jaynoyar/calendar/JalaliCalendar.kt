package com.example.jaynoyar.calendar

import java.time.LocalDate

/**
 * Jalali (Shamsi) Date representation with exact conversion algorithms.
 */
data class JalaliDate(
    val year: Int,
    val month: Int,
    val day: Int
) : Comparable<JalaliDate> {

    init {
        require(month in 1..12) { "Month must be between 1 and 12" }
        require(day in 1..31) { "Day must be between 1 and 31" }
    }

    override fun compareTo(other: JalaliDate): Int {
        if (year != other.year) return year.compareTo(other.year)
        if (month != other.month) return month.compareTo(other.month)
        return day.compareTo(other.day)
    }

    /**
     * Formats date as YYYY/MM/DD with 2-digit month and day
     */
    fun formatFormatted(usePersianDigits: Boolean = true): String {
        val str = "%04d/%02d/%02d".format(year, month, day)
        return if (usePersianDigits) DateUtils.toPersianDigits(str) else str
    }

    /**
     * Formats with Persian month name, e.g. "۷ آبان ۱۴۰۵"
     */
    fun formatWithMonthName(usePersianDigits: Boolean = true): String {
        val monthName = PersianCalendarConstants.PERSIAN_MONTH_NAMES.getOrElse(month - 1) { "" }
        val dayStr = if (usePersianDigits) DateUtils.toPersianDigits(day.toString()) else day.toString()
        val yearStr = if (usePersianDigits) DateUtils.toPersianDigits(year.toString()) else year.toString()
        return "$dayStr $monthName $yearStr"
    }

    /**
     * Converts to Gregorian LocalDate
     */
    fun toLocalDate(): LocalDate {
        return DateUtils.jalaliToGregorian(this)
    }

    companion object {
        fun now(): JalaliDate {
            return DateUtils.gregorianToJalali(LocalDate.now())
        }

        fun fromLocalDate(localDate: LocalDate): JalaliDate {
            return DateUtils.gregorianToJalali(localDate)
        }

        fun parse(str: String): JalaliDate? {
            val clean = DateUtils.toEnglishDigits(str).trim()
            val parts = clean.split("/", "-")
            if (parts.size == 3) {
                val y = parts[0].toIntOrNull() ?: return null
                val m = parts[1].toIntOrNull() ?: return null
                val d = parts[2].toIntOrNull() ?: return null
                if (m in 1..12 && d in 1..31) {
                    return JalaliDate(y, m, d)
                }
            }
            return null
        }
    }
}

object PersianCalendarConstants {
    val PERSIAN_MONTH_NAMES = listOf(
        "فروردین", "اردیبهشت", "خرداد",
        "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر",
        "دی", "بهمن", "اسفند"
    )

    val PERSIAN_WEEKDAY_NAMES = listOf(
        "شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنج‌شنبه", "جمعه"
    )

    val PERSIAN_WEEKDAY_SHORT = listOf(
        "ش", "ی", "د", "س", "چ", "پ", "ج"
    )
}

object DateUtils {

    fun toPersianDigits(input: String): String {
        val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        val sb = StringBuilder()
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(persianDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun toEnglishDigits(input: String): String {
        val sb = StringBuilder()
        for (ch in input) {
            when (ch) {
                '۰' -> sb.append('0')
                '۱' -> sb.append('1')
                '۲' -> sb.append('2')
                '۳' -> sb.append('3')
                '۴' -> sb.append('4')
                '۵' -> sb.append('5')
                '۶' -> sb.append('6')
                '۷' -> sb.append('7')
                '۸' -> sb.append('8')
                '۹' -> sb.append('9')
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Determines whether a Jalali year is leap year
     */
    fun isJalaliLeapYear(year: Int): Boolean {
        val r = (((year - 474) % 2820 + 2820) % 2820) % 33
        return r in intArrayOf(1, 5, 9, 13, 17, 22, 26, 30)
    }

    fun getDaysInJalaliMonth(year: Int, month: Int): Int {
        return when {
            month in 1..6 -> 31
            month in 7..11 -> 30
            month == 12 -> if (isJalaliLeapYear(year)) 30 else 29
            else -> 30
        }
    }

    /**
     * Converts Gregorian LocalDate to JalaliDate using standard astronomical Julian Day algorithm.
     */
    fun gregorianToJalali(date: LocalDate): JalaliDate {
        val g_y = date.year
        val g_m = date.monthValue
        val g_d = date.dayOfMonth

        val g_days_in_month = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val j_days_in_month = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        val gy = g_y - 1600
        val gm = g_m - 1
        val gd = g_d - 1

        var g_day_no = 365 * gy + (gy + 3) / 4 - (gy + 99) / 100 + (gy + 399) / 400

        for (i in 0 until gm) {
            g_day_no += g_days_in_month[i]
        }
        if (gm > 1 && ((g_y % 4 == 0 && g_y % 100 != 0) || (g_y % 400 == 0))) {
            g_day_no++
        }
        g_day_no += gd

        var j_day_no = g_day_no - 79

        val j_np = j_day_no / 12053
        j_day_no %= 12053

        var jy = 979 + 33 * j_np + 4 * (j_day_no / 1461)
        j_day_no %= 1461

        if (j_day_no >= 366) {
            jy += (j_day_no - 1) / 365
            j_day_no = (j_day_no - 1) % 365
        }

        var jm = 0
        for (i in 0 until 11) {
            if (j_day_no >= j_days_in_month[i]) {
                j_day_no -= j_days_in_month[i]
                jm++
            } else {
                break
            }
        }
        val jd = j_day_no + 1
        return JalaliDate(jy, jm + 1, jd)
    }

    fun jalaliToGregorian(jalaliDate: JalaliDate): LocalDate {
        val j_y = jalaliDate.year
        val j_m = jalaliDate.month
        val j_d = jalaliDate.day

        val g_days_in_month = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val j_days_in_month = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        val jy = j_y - 979
        val jm = j_m - 1
        val jd = j_d - 1

        var j_day_no = 365 * jy + (jy / 33) * 8 + ((jy % 33) + 3) / 4
        for (i in 0 until jm) {
            j_day_no += j_days_in_month[i]
        }
        j_day_no += jd

        var g_day_no = j_day_no + 79

        var gy = 1600 + 400 * (g_day_no / 146097)
        g_day_no %= 146097

        var leap = true
        if (g_day_no >= 36525) {
            g_day_no--
            gy += 100 * (g_day_no / 36524)
            g_day_no %= 36524

            if (g_day_no >= 365) {
                g_day_no++
            } else {
                leap = false
            }
        }

        gy += 4 * (g_day_no / 1461)
        g_day_no %= 1461

        if (g_day_no >= 366) {
            leap = false
            g_day_no--
            gy += g_day_no / 365
            g_day_no %= 365
        }

        var gm = 0
        for (i in 0 until 12) {
            val daysInMonth = if (i == 1 && leap) 29 else g_days_in_month[i]
            if (g_day_no >= daysInMonth) {
                g_day_no -= daysInMonth
                gm++
            } else {
                break
            }
        }
        val gd = g_day_no + 1
        return LocalDate.of(gy, gm + 1, gd)
    }

    /**
     * Returns Persian weekday index (0 for Saturday, 6 for Friday)
     */
    fun getPersianWeekdayIndex(date: LocalDate): Int {
        // DayOfWeek.MONDAY = 1 ... SUNDAY = 7
        // Saturday in DayOfWeek is 6.
        // Persian week: Saturday=0, Sunday=1, Monday=2, Tuesday=3, Wednesday=4, Thursday=5, Friday=6
        return when (date.dayOfWeek) {
            java.time.DayOfWeek.SATURDAY -> 0
            java.time.DayOfWeek.SUNDAY -> 1
            java.time.DayOfWeek.MONDAY -> 2
            java.time.DayOfWeek.TUESDAY -> 3
            java.time.DayOfWeek.WEDNESDAY -> 4
            java.time.DayOfWeek.THURSDAY -> 5
            java.time.DayOfWeek.FRIDAY -> 6
        }
    }

    fun getPersianWeekdayName(date: LocalDate): String {
        return PersianCalendarConstants.PERSIAN_WEEKDAY_NAMES[getPersianWeekdayIndex(date)]
    }

    fun formatDurationMinutes(minutes: Int, usePersianDigits: Boolean = true): String {
        val h = minutes / 60
        val m = minutes % 60
        val text = when {
            h > 0 && m > 0 -> "$h ساعت و $m دقیقه"
            h > 0 -> "$h ساعت"
            m > 0 -> "$m دقیقه"
            else -> "۰ دقیقه"
        }
        return if (usePersianDigits) toPersianDigits(text) else text
    }

    fun formatTimeDigital(minutes: Int, usePersianDigits: Boolean = true): String {
        val h = minutes / 60
        val m = minutes % 60
        val text = "%02d:%02d".format(h, m)
        return if (usePersianDigits) toPersianDigits(text) else text
    }
}
