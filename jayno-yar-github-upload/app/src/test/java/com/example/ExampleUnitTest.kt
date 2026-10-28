package com.example

import com.example.jaynoyar.calculator.AttendanceCalculator
import com.example.jaynoyar.calendar.DateUtils
import com.example.jaynoyar.calendar.JalaliDate
import com.example.jaynoyar.data.SeedData
import com.example.jaynoyar.data.model.AttendanceEntity
import com.example.jaynoyar.data.model.LeaveRecordEntity
import com.example.jaynoyar.excel.XlsxEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.time.LocalDate

class ExampleUnitTest {

    private val defaultSchedule = SeedData.DEFAULT_SCHEDULE

    @Test
    fun testExactExpectedWorkingDuration() {
        val saturday = LocalDate.of(2026, 10, 24) // Saturday (standard work day)
        val expectedSat = AttendanceCalculator.determineExpectedWorkingMinutes(saturday, defaultSchedule)
        assertEquals(480, expectedSat) // 8 hours net

        val thursday = LocalDate.of(2026, 10, 29) // Thursday
        val expectedThu = AttendanceCalculator.determineExpectedWorkingMinutes(thursday, defaultSchedule)
        assertEquals(240, expectedThu) // 4 hours half day

        val friday = LocalDate.of(2026, 10, 30) // Friday (holiday)
        val expectedFri = AttendanceCalculator.determineExpectedWorkingMinutes(friday, defaultSchedule)
        assertEquals(0, expectedFri)
    }

    @Test
    fun testPositiveOvertimeCalculation() {
        // Expected net = 480 minutes (8h). Actual check-in 08:00, check-out 18:00 (10h gross = 600m), break 45m unpaid.
        // Net = 600 - 45 = 555 minutes.
        // Overtime = 555 - 480 = 75 minutes.
        val gross = AttendanceCalculator.calculateGrossDurationMinutes(8 * 60, 18 * 60)
        assertEquals(600, gross)

        val net = AttendanceCalculator.calculateNetDurationMinutes(gross, 45)
        assertEquals(555, net)

        val overtime = AttendanceCalculator.calculateDailyOvertimeMinutes(net, 480, minOvertimeThreshold = 15)
        val undertime = AttendanceCalculator.calculateDailyUndertimeMinutes(net, 480)

        assertEquals(75, overtime)
        assertEquals(0, undertime)
    }

    @Test
    fun testPositiveUndertimeCalculation() {
        // Expected net = 480 min. Actual check-in 08:00, check-out 15:00 (7h = 420m), break 45m.
        // Net = 420 - 45 = 375 minutes.
        // Undertime = 480 - 375 = 105 minutes.
        val gross = AttendanceCalculator.calculateGrossDurationMinutes(8 * 60, 15 * 60)
        val net = AttendanceCalculator.calculateNetDurationMinutes(gross, 45)
        val overtime = AttendanceCalculator.calculateDailyOvertimeMinutes(net, 480)
        val undertime = AttendanceCalculator.calculateDailyUndertimeMinutes(net, 480)

        assertEquals(375, net)
        assertEquals(0, overtime)
        assertEquals(105, undertime)
    }

    @Test
    fun testLateArrivalCombinedWithOvertime() {
        // Employee arrives 30 minutes late (08:30 instead of 08:00).
        // Leaves at 18:30 (10h gross = 600m), 45m unpaid break.
        // Net = 600 - 45 = 555 min. Expected = 480 min.
        // Invariant: Employee has 30 minutes lateness AND 75 minutes overtime! Overtime does not erase lateness.
        val checkInMin = 8 * 60 + 30
        val checkOutMin = 18 * 60 + 30
        val lateness = AttendanceCalculator.calculateLateArrivalMinutes(checkInMin, 8 * 60)
        assertEquals(30, lateness)

        val gross = AttendanceCalculator.calculateGrossDurationMinutes(checkInMin, checkOutMin)
        val net = AttendanceCalculator.calculateNetDurationMinutes(gross, 45)
        val overtime = AttendanceCalculator.calculateDailyOvertimeMinutes(net, 480)

        assertEquals(75, overtime)
    }

    @Test
    fun testPaidAndUnpaidBreaks() {
        // 45m unpaid lunch break -> subtracted from gross.
        // 15m paid tea break -> NOT subtracted from gross.
        val gross = 480
        val netUnpaid = AttendanceCalculator.calculateNetDurationMinutes(gross, unpaidBreakMinutes = 45)
        assertEquals(435, netUnpaid)

        // Paid break has unpaidBreakMinutes = 0
        val netPaid = AttendanceCalculator.calculateNetDurationMinutes(gross, unpaidBreakMinutes = 0)
        assertEquals(480, netPaid)
    }

    @Test
    fun testOvernightShiftAcrossMidnight() {
        // Shift starts at 22:00 (1320m) and ends at 06:00 (360m) next day.
        // Duration: (1440 - 1320) + 360 = 120 + 360 = 480 minutes (8 hours).
        val gross = AttendanceCalculator.calculateGrossDurationMinutes(22 * 60, 6 * 60, isExplicitOvernight = true)
        assertEquals(480, gross)
    }

    @Test
    fun testApprovedFullDayAndHourlyLeave() {
        val sat = LocalDate.of(2026, 10, 24)

        // Full day approved leave reduces expected minutes to 0
        val dailyLeave = LeaveRecordEntity(
            employeeId = "EMP-101",
            startDate = "2026-10-24",
            endDate = "2026-10-24",
            leaveType = "DAILY",
            status = "APPROVED"
        )
        val expectedWithFullLeave = AttendanceCalculator.determineExpectedWorkingMinutes(
            sat,
            defaultSchedule,
            leaveRecord = dailyLeave
        )
        assertEquals(0, expectedWithFullLeave)

        // Hourly approved leave (2 hours = 120 min) reduces expected minutes by 120 (480 - 120 = 360)
        val hourlyLeave = LeaveRecordEntity(
            employeeId = "EMP-101",
            startDate = "2026-10-24",
            endDate = "2026-10-24",
            startTime = "10:00",
            endTime = "12:00",
            leaveType = "HOURLY",
            status = "APPROVED"
        )
        val expectedWithHourlyLeave = AttendanceCalculator.determineExpectedWorkingMinutes(
            sat,
            defaultSchedule,
            leaveRecord = hourlyLeave
        )
        assertEquals(360, expectedWithHourlyLeave)
    }

    @Test
    fun testJalaliDateConversionAndLeapYear() {
        // Test date: 2026-10-28 should convert to Jalali 1405/08/07
        val gDate = LocalDate.of(2026, 10, 28)
        val jDate = JalaliDate.fromLocalDate(gDate)
        assertEquals(1405, jDate.year)
        assertEquals(8, jDate.month) // Aban
        assertEquals(6, jDate.day)

        // Reverse conversion: Jalali back to Gregorian 2026-10-28
        val convertedBack = jDate.toLocalDate()
        assertEquals(gDate, convertedBack)

        // Leap year check: Jalali 1399 was leap year, 1400 was not
        assertTrue(DateUtils.isJalaliLeapYear(1399))
        assertFalse(DateUtils.isJalaliLeapYear(1400))
    }

    @Test
    fun testXlsxOpenXmlGenerationAndIntegrity() {
        val headers = listOf("کد پرسنلی", "نام پرسنل", "کارکرد خالص")
        val rows = listOf(
            listOf("EMP-101", "استاد کریمی", 480),
            listOf("EMP-102", "رضا نوری", 510)
        )

        val bytes = XlsxEngine.createWorkbookBytes("تست کارگاه", headers, rows)
        assertNotNull(bytes)
        assertTrue(bytes.isNotEmpty())

        // Verify PK zip header signature (0x50, 0x4B, 0x03, 0x04)
        assertEquals(0x50.toByte(), bytes[0])
        assertEquals(0x4B.toByte(), bytes[1])
        assertEquals(0x03.toByte(), bytes[2])
        assertEquals(0x04.toByte(), bytes[3])
    }
}
