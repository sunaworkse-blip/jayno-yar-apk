package com.example.jaynoyar.calculator

import com.example.jaynoyar.calendar.DateUtils
import com.example.jaynoyar.data.model.AttendanceEntity
import com.example.jaynoyar.data.model.LeaveRecordEntity
import com.example.jaynoyar.data.model.WorkScheduleEntity
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.max

/**
 * Robust mathematical calculation engine for Jayno Furniture Workshop attendance.
 * All computations use integer minutes to eliminate floating point rounding inaccuracies.
 */
object AttendanceCalculator {

    /**
     * Parses time string (e.g. "08:30" or "8:30") to minute of day (0..1439).
     */
    fun parseTimeToMinutes(timeStr: String?): Int? {
        if (timeStr.isNullOrBlank()) return null
        val clean = DateUtils.toEnglishDigits(timeStr).trim()
        val parts = clean.split(":")
        if (parts.size >= 2) {
            val h = parts[0].toIntOrNull() ?: return null
            val m = parts[1].toIntOrNull() ?: return null
            if (h in 0..23 && m in 0..59) {
                return h * 60 + m
            }
        }
        return null
    }

    /**
     * Converts minute of day to HH:mm string.
     */
    fun minutesToTimeString(minutes: Int): String {
        val normalized = ((minutes % 1440) + 1440) % 1440
        val h = normalized / 60
        val m = normalized % 60
        return "%02d:%02d".format(h, m)
    }

    /**
     * Calculates gross attendance duration in integer minutes.
     * Accurately supports overnight shifts crossing midnight (e.g. 22:00 to 06:00 = 8h / 480m).
     */
    fun calculateGrossDurationMinutes(
        checkInMinutes: Int?,
        checkOutMinutes: Int?,
        isExplicitOvernight: Boolean = false
    ): Int {
        if (checkInMinutes == null || checkOutMinutes == null) return 0
        return if (checkOutMinutes >= checkInMinutes && !isExplicitOvernight) {
            checkOutMinutes - checkInMinutes
        } else {
            // Crossing midnight
            (1440 - checkInMinutes) + checkOutMinutes
        }
    }

    /**
     * Calculates net working duration in integer minutes.
     * Invariant: Net duration = max(0, gross - unpaidBreak)
     * Paid breaks are NEVER deducted.
     */
    fun calculateNetDurationMinutes(
        grossMinutes: Int,
        unpaidBreakMinutes: Int
    ): Int {
        return max(0, grossMinutes - max(0, unpaidBreakMinutes))
    }

    /**
     * Determines expected working minutes for a given date, day of week, schedule, and leave.
     */
    fun determineExpectedWorkingMinutes(
        date: LocalDate,
        schedule: WorkScheduleEntity,
        isHoliday: Boolean = false,
        leaveRecord: LeaveRecordEntity? = null
    ): Int {
        // Friday in Persian calendar is holiday (DayOfWeek.FRIDAY)
        val isFriday = date.dayOfWeek == java.time.DayOfWeek.FRIDAY
        if (isFriday && schedule.fridayIsHoliday) {
            return 0
        }
        if (isHoliday) {
            return 0
        }

        // Check if full-day approved leave exists
        if (leaveRecord != null && leaveRecord.status == "APPROVED") {
            if (leaveRecord.leaveType in listOf("DAILY", "SICK", "MISSION")) {
                return 0
            }
        }

        // Thursday half-day logic
        val isThursday = date.dayOfWeek == java.time.DayOfWeek.THURSDAY
        var baseExpected = if (isThursday) {
            schedule.thursdayExpectedMinutes
        } else {
            schedule.dailyExpectedMinutes
        }

        // Hourly leave deduction from expected duration
        if (leaveRecord != null && leaveRecord.status == "APPROVED" && leaveRecord.leaveType == "HOURLY") {
            val startMin = parseTimeToMinutes(leaveRecord.startTime) ?: 0
            val endMin = parseTimeToMinutes(leaveRecord.endTime) ?: 0
            val hourlyLeaveMinutes = if (endMin >= startMin) endMin - startMin else (1440 - startMin) + endMin
            baseExpected = max(0, baseExpected - hourlyLeaveMinutes)
        }

        return baseExpected
    }

    /**
     * Computes late arrival minutes relative to scheduled start time.
     * Does NOT depend on overtime; punctuality deviation is independently preserved.
     */
    fun calculateLateArrivalMinutes(
        actualCheckInMinutes: Int?,
        scheduledStartMinutes: Int,
        gracePeriodMinutes: Int = 0
    ): Int {
        if (actualCheckInMinutes == null) return 0
        val diff = actualCheckInMinutes - scheduledStartMinutes
        return if (diff > gracePeriodMinutes) diff else 0
    }

    /**
     * Computes early departure minutes relative to scheduled end time.
     */
    fun calculateEarlyDepartureMinutes(
        actualCheckOutMinutes: Int?,
        scheduledEndMinutes: Int
    ): Int {
        if (actualCheckOutMinutes == null) return 0
        val diff = scheduledEndMinutes - actualCheckOutMinutes
        return if (diff > 0) diff else 0
    }

    /**
     * Calculates daily overtime in integer minutes.
     * Overtime = max(0, netWorkingMinutes - expectedMinutes), subject to minimum threshold.
     */
    fun calculateDailyOvertimeMinutes(
        netWorkingMinutes: Int,
        expectedMinutes: Int,
        minOvertimeThreshold: Int = 15
    ): Int {
        if (netWorkingMinutes <= expectedMinutes) return 0
        val rawDiff = netWorkingMinutes - expectedMinutes
        return if (rawDiff >= minOvertimeThreshold) rawDiff else 0
    }

    /**
     * Calculates daily undertime in integer minutes.
     * Undertime = max(0, expectedMinutes - netWorkingMinutes).
     */
    fun calculateDailyUndertimeMinutes(
        netWorkingMinutes: Int,
        expectedMinutes: Int
    ): Int {
        if (netWorkingMinutes >= expectedMinutes) return 0
        return expectedMinutes - netWorkingMinutes
    }

    /**
     * Comprehensive calculation method providing full audit breakdown.
     */
    fun computeBreakdown(
        attendance: AttendanceEntity,
        schedule: WorkScheduleEntity,
        isHoliday: Boolean = false,
        leaveRecord: LeaveRecordEntity? = null,
        minOvertimeThreshold: Int = 15
    ): AttendanceCalculationBreakdown {
        val checkInMin = parseTimeToMinutes(attendance.checkInTime)
        val checkOutMin = parseTimeToMinutes(attendance.checkOutTime)

        val isIncomplete = (checkInMin != null && checkOutMin == null) || (checkInMin == null && checkOutMin != null)
        val isAbsent = (checkInMin == null && checkOutMin == null)

        val localDate = LocalDate.parse(attendance.workDate)
        val expectedMinutes = determineExpectedWorkingMinutes(localDate, schedule, isHoliday, leaveRecord)

        val grossMinutes = if (!isIncomplete && !isAbsent) {
            calculateGrossDurationMinutes(checkInMin, checkOutMin, attendance.isOvernight)
        } else {
            0
        }

        val unpaidBreakMinutes = if (attendance.isBreakPaid) 0 else attendance.breakMinutes
        val paidBreakMinutes = if (attendance.isBreakPaid) attendance.breakMinutes else 0

        val netWorkingMinutes = if (!isIncomplete && !isAbsent) {
            calculateNetDurationMinutes(grossMinutes, unpaidBreakMinutes)
        } else {
            0
        }

        val overtimeMinutes = if (!isIncomplete && !isAbsent) {
            calculateDailyOvertimeMinutes(netWorkingMinutes, expectedMinutes, minOvertimeThreshold)
        } else {
            0
        }

        val undertimeMinutes = if (!isIncomplete) {
            calculateDailyUndertimeMinutes(netWorkingMinutes, expectedMinutes)
        } else {
            // Incomplete attendance is flagged as undertime for missing portion or full day until resolved
            expectedMinutes
        }

        val schedStart = parseTimeToMinutes(schedule.standardStartTime) ?: (8 * 60)
        val schedEnd = parseTimeToMinutes(schedule.standardEndTime) ?: (17 * 60)

        val latenessMinutes = calculateLateArrivalMinutes(checkInMin, schedStart)
        val earlyDepartureMinutes = calculateEarlyDepartureMinutes(checkOutMin, schedEnd)

        val leaveAdjustment = if (leaveRecord != null && leaveRecord.status == "APPROVED") {
            if (leaveRecord.leaveType in listOf("DAILY", "SICK", "MISSION")) expectedMinutes else 0
        } else 0

        val ruleDescription = buildString {
            append("برنامه کاری: ").append(schedule.title)
            append(" | موظفی: ").append(expectedMinutes).append(" دقیقه")
            if (unpaidBreakMinutes > 0) {
                append(" | کسر استراحت ناهار: ").append(unpaidBreakMinutes).append(" دقیقه")
            }
            if (paidBreakMinutes > 0) {
                append(" | استراحت چای (محاسبه به عنوان کارکرد پرداخت‌شده): ").append(paidBreakMinutes).append(" دقیقه")
            }
            if (latenessMinutes > 0) {
                append(" | تاخیر ثبت‌شده: ").append(latenessMinutes).append(" دقیقه")
            }
            if (overtimeMinutes > 0) {
                append(" | اضافه‌کار واجد شرایط: ").append(overtimeMinutes).append(" دقیقه")
            }
        }

        val approvedOvertime = if (attendance.overtimeApproved) {
            attendance.approvedOvertimeMinutes ?: overtimeMinutes
        } else {
            0
        }

        return AttendanceCalculationBreakdown(
            employeeId = attendance.employeeId,
            dateString = attendance.workDate,
            checkInTime = attendance.checkInTime,
            checkOutTime = attendance.checkOutTime,
            grossMinutes = grossMinutes,
            unpaidBreakMinutes = unpaidBreakMinutes,
            paidBreakMinutes = paidBreakMinutes,
            netWorkingMinutes = netWorkingMinutes,
            expectedMinutes = expectedMinutes,
            overtimeMinutes = overtimeMinutes,
            undertimeMinutes = undertimeMinutes,
            latenessMinutes = latenessMinutes,
            earlyDepartureMinutes = earlyDepartureMinutes,
            isOvernight = attendance.isOvernight,
            isIncomplete = isIncomplete,
            isAbsent = isAbsent,
            leaveAdjustmentMinutes = leaveAdjustment,
            leaveTypeApplied = leaveRecord?.leaveType,
            appliedRuleDescription = ruleDescription,
            isOvertimeApproved = attendance.overtimeApproved,
            approvedOvertimeMinutes = approvedOvertime
        )
    }
}
