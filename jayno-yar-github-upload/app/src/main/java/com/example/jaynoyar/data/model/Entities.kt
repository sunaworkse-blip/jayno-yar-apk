package com.example.jaynoyar.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey
    val id: String, // e.g. "EMP-101"
    val firstName: String,
    val lastName: String,
    val department: String, // e.g. "نجاری و برش", "رنگ‌کاری و پلی‌استر", "رویه‌کوبی و خیاطی", "مونتاژ و بسته‌بندی", "مدیریت و اداری"
    val role: String, // e.g. "استادکار", "تکنسین", "کمک‌نجار", "سرپرست واحد"
    val nationalId: String,
    val phone: String,
    val hireDate: String, // YYYY-MM-DD
    val status: String, // "ACTIVE", "ON_LEAVE", "ARCHIVED"
    val baseHourlyWage: Long, // Tomans per hour
    val scheduleId: String = "DEFAULT_WORKSHOP",
    val notes: String = ""
) {
    val fullName: String
        get() = "$firstName $lastName"
}

@Entity(
    tableName = "attendances",
    indices = [Index(value = ["employeeId", "workDate"], unique = true)]
)
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val employeeId: String,
    val workDate: String, // YYYY-MM-DD
    val checkInTime: String?, // "08:00"
    val checkOutTime: String?, // "17:30"
    val breakMinutes: Int = 45,
    val isBreakPaid: Boolean = false,
    val leaveId: Long? = null,
    val isOvernight: Boolean = false,
    val overtimeApproved: Boolean = true,
    val approvedOvertimeMinutes: Int? = null,
    val note: String? = null,
    val lastModifiedTimestamp: Long = System.currentTimeMillis(),
    val modifiedBy: String = "مدیر کارگاه"
)

@Entity(tableName = "work_schedules")
data class WorkScheduleEntity(
    @PrimaryKey
    val id: String, // "DEFAULT_WORKSHOP"
    val title: String, // "شیفت استاندارد کارگاه مبلمان جاینو"
    val standardStartTime: String = "08:00",
    val standardEndTime: String = "17:00",
    val dailyExpectedMinutes: Int = 480, // 8 hours net
    val lunchBreakMinutes: Int = 45,
    val teaBreakMinutes: Int = 15,
    val workingDaysOfWeek: String = "6,7,1,2,3,4", // Saturday to Thursday
    val thursdayExpectedMinutes: Int = 240, // 4 hours on Thursday
    val fridayIsHoliday: Boolean = true,
    val effectiveFromDate: String = "2024-01-01"
)

@Entity(tableName = "leave_records")
data class LeaveRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val employeeId: String,
    val startDate: String, // YYYY-MM-DD
    val endDate: String, // YYYY-MM-DD
    val startTime: String? = null, // "10:00" for hourly
    val endTime: String? = null, // "12:00" for hourly
    val leaveType: String, // "DAILY", "HOURLY", "SICK", "UNPAID", "MISSION"
    val status: String, // "APPROVED", "PENDING", "REJECTED"
    val reason: String = "",
    val approvedBy: String = "مهندس کریمی"
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val actionType: String, // "CREATE", "UPDATE", "DELETE", "IMPORT", "CALCULATION", "RESTORE"
    val targetEntity: String, // "ATTENDANCE", "EMPLOYEE", "SCHEDULE", "LEAVE"
    val description: String,
    val operatorName: String = "مدیر سیستم"
)
