package com.example.jaynoyar.calculator

import java.time.LocalTime

/**
 * Breakdown of all calculated components for complete audit transparency.
 */
data class AttendanceCalculationBreakdown(
    val employeeId: String,
    val dateString: String,
    val checkInTime: String?,
    val checkOutTime: String?,
    val grossMinutes: Int,
    val unpaidBreakMinutes: Int,
    val paidBreakMinutes: Int,
    val netWorkingMinutes: Int,
    val expectedMinutes: Int,
    val overtimeMinutes: Int,
    val undertimeMinutes: Int,
    val latenessMinutes: Int,
    val earlyDepartureMinutes: Int,
    val isOvernight: Boolean,
    val isIncomplete: Boolean,
    val isAbsent: Boolean,
    val leaveAdjustmentMinutes: Int,
    val leaveTypeApplied: String?,
    val appliedRuleDescription: String,
    val isOvertimeApproved: Boolean,
    val approvedOvertimeMinutes: Int
) {
    val netHoursDecimal: Double
        get() = netWorkingMinutes / 60.0

    val overtimeHoursDecimal: Double
        get() = overtimeMinutes / 60.0

    val undertimeHoursDecimal: Double
        get() = undertimeMinutes / 60.0
}

enum class AttendanceStatus(val titleFa: String) {
    PRESENT("حاضر"),
    ABSENT("غایب"),
    INCOMPLETE("تردد ناقص"),
    ON_LEAVE("مرخصی"),
    HOLIDAY("تعطیل رسمی"),
    WEEKEND("تعطیل هفتگی (جمعه)")
}
