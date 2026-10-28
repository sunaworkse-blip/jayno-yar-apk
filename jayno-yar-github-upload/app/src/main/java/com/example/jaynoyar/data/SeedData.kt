package com.example.jaynoyar.data

import com.example.jaynoyar.calendar.JalaliDate
import com.example.jaynoyar.data.model.AttendanceEntity
import com.example.jaynoyar.data.model.AuditLogEntity
import com.example.jaynoyar.data.model.EmployeeEntity
import com.example.jaynoyar.data.model.LeaveRecordEntity
import com.example.jaynoyar.data.model.WorkScheduleEntity
import java.time.LocalDate

object SeedData {

    val DEFAULT_SCHEDULE = WorkScheduleEntity(
        id = "DEFAULT_WORKSHOP",
        title = "شیفت استاندارد کارگاه مبلمان جاینو",
        standardStartTime = "08:00",
        standardEndTime = "17:00",
        dailyExpectedMinutes = 480, // 8 hours net
        lunchBreakMinutes = 45, // unpaid
        teaBreakMinutes = 15, // paid
        workingDaysOfWeek = "6,7,1,2,3,4", // Saturday to Thursday
        thursdayExpectedMinutes = 240, // 4 hours on Thursday
        fridayIsHoliday = true,
        effectiveFromDate = "2024-01-01"
    )

    val INITIAL_EMPLOYEES = listOf(
        EmployeeEntity(
            id = "EMP-101",
            firstName = "احمد",
            lastName = "کریمی",
            department = "نجاری و ساخت",
            role = "استادکار ارشد چوب",
            nationalId = "0018934201",
            phone = "09121112233",
            hireDate = "2021-03-21",
            status = "ACTIVE",
            baseHourlyWage = 145000,
            notes = "سرپرست سالن نجاری چوب راش و گردو"
        ),
        EmployeeEntity(
            id = "EMP-102",
            firstName = "رضا",
            lastName = "نوری",
            department = "رنگ‌کاری و پلی‌استر",
            role = "استاد رنگ‌کار و پولیش",
            nationalId = "0027734190",
            phone = "09122223344",
            hireDate = "2021-06-15",
            status = "ACTIVE",
            baseHourlyWage = 135000,
            notes = "متخصص رنگ فندقی، ورق طلا و پلی‌استر سوپر مات"
        ),
        EmployeeEntity(
            id = "EMP-103",
            firstName = "مهدی",
            lastName = "سلطانی",
            department = "رویه‌کوبی و خیاطی",
            role = "رویه‌کوب ارشد مبلمان",
            nationalId = "0036629811",
            phone = "09123334455",
            hireDate = "2022-01-10",
            status = "ACTIVE",
            baseHourlyWage = 140000,
            notes = "مسئول رویه‌کوبی مبل چستر و لمسه دست‌دوز"
        ),
        EmployeeEntity(
            id = "EMP-104",
            firstName = "علی‌اصغر",
            lastName = "داوودی",
            department = "نجاری و ساخت",
            role = "اپراتور برش و CNC",
            nationalId = "0045518722",
            phone = "09124445566",
            hireDate = "2022-08-01",
            status = "ACTIVE",
            baseHourlyWage = 120000,
            notes = "برش دقیق اسکلت و پایه‌های خراطی"
        ),
        EmployeeEntity(
            id = "EMP-105",
            firstName = "حسین",
            lastName = "محمدی",
            department = "رویه‌کوبی و خیاطی",
            role = "خیاط تخصصی چرم و مخمل",
            nationalId = "0054407633",
            phone = "09125556677",
            hireDate = "2023-02-14",
            status = "ACTIVE",
            baseHourlyWage = 115000,
            notes = "دوخت روکش و کوسن‌های دکوراتیو"
        ),
        EmployeeEntity(
            id = "EMP-106",
            firstName = "محسن",
            lastName = "باقری",
            department = "مونتاژ و بسته‌بندی",
            role = "مسئول مونتاژ نهایی",
            nationalId = "0063396544",
            phone = "09126667788",
            hireDate = "2023-05-20",
            status = "ACTIVE",
            baseHourlyWage = 110000,
            notes = "مونتاژ یراق‌آلات، لفاف‌پیچی و بسته‌بندی صادراتی"
        ),
        EmployeeEntity(
            id = "EMP-107",
            firstName = "علیرضا",
            lastName = "نجفی",
            department = "نجاری و ساخت",
            role = "کمک‌نجار و سمباده‌زن",
            nationalId = "0072285455",
            phone = "09127778899",
            hireDate = "2023-11-01",
            status = "ACTIVE",
            baseHourlyWage = 95000,
            notes = "پرداخت و سمباده‌کاری کلاف خام"
        ),
        EmployeeEntity(
            id = "EMP-108",
            firstName = "سارا",
            lastName = "معتمدی",
            department = "مدیریت و اداری",
            role = "مدیر داخلی و هماهنگ‌کننده سفارشات",
            nationalId = "0081174366",
            phone = "09128889900",
            hireDate = "2022-04-10",
            status = "ACTIVE",
            baseHourlyWage = 130000,
            notes = "ثبت فاکتور، پیگیری سفارشات نمایشگاه و ثبت تردد پرسنل"
        )
    )

    fun createInitialAttendances(): List<AttendanceEntity> {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val twoDaysAgo = today.minusDays(2)
        val threeDaysAgo = today.minusDays(3)

        return listOf(
            // Today attendances
            AttendanceEntity(
                employeeId = "EMP-101",
                workDate = today.toString(),
                checkInTime = "07:55",
                checkOutTime = "18:30", // 1h 30m overtime
                breakMinutes = 45,
                isBreakPaid = false,
                overtimeApproved = true,
                note = "پروژه ست مبلمان کلاسیک سالن شماره ۲"
            ),
            AttendanceEntity(
                employeeId = "EMP-102",
                workDate = today.toString(),
                checkInTime = "08:20", // 20m late
                checkOutTime = "18:00", // 40m overtime
                breakMinutes = 45,
                isBreakPaid = false,
                overtimeApproved = true,
                note = "تاخیر با هماهنگی به دلیل ترافیک آزادراه"
            ),
            AttendanceEntity(
                employeeId = "EMP-103",
                workDate = today.toString(),
                checkInTime = "08:00",
                checkOutTime = "17:00", // Exactly standard 8 hours
                breakMinutes = 45,
                isBreakPaid = false,
                overtimeApproved = true
            ),
            AttendanceEntity(
                employeeId = "EMP-104",
                workDate = today.toString(),
                checkInTime = "08:02",
                checkOutTime = "17:15",
                breakMinutes = 45,
                isBreakPaid = false,
                overtimeApproved = true
            ),
            AttendanceEntity(
                employeeId = "EMP-105",
                workDate = today.toString(),
                checkInTime = "08:10",
                checkOutTime = null, // Incomplete punch (still working)
                breakMinutes = 45,
                isBreakPaid = false,
                note = "تردد باز - در انتظار ثبت خروج پایان شیفت"
            ),
            AttendanceEntity(
                employeeId = "EMP-106",
                workDate = today.toString(),
                checkInTime = "08:00",
                checkOutTime = "16:00", // Early departure (1h undertime)
                breakMinutes = 45,
                isBreakPaid = false,
                note = "خروج زودهنگام به دلیل نوبت پزشکی"
            ),
            AttendanceEntity(
                employeeId = "EMP-108",
                workDate = today.toString(),
                checkInTime = "08:30",
                checkOutTime = "17:30",
                breakMinutes = 45,
                isBreakPaid = false,
                overtimeApproved = true
            ),

            // Yesterday
            AttendanceEntity(
                employeeId = "EMP-101",
                workDate = yesterday.toString(),
                checkInTime = "08:00",
                checkOutTime = "19:00", // 2h overtime
                breakMinutes = 45,
                isBreakPaid = false,
                overtimeApproved = true,
                note = "تکمیل کلاف مبل ال سفارشی"
            ),
            AttendanceEntity(
                employeeId = "EMP-102",
                workDate = yesterday.toString(),
                checkInTime = "08:00",
                checkOutTime = "17:00",
                breakMinutes = 45,
                isBreakPaid = false,
                overtimeApproved = true
            ),
            AttendanceEntity(
                employeeId = "EMP-103",
                workDate = yesterday.toString(),
                checkInTime = "08:00",
                checkOutTime = "17:45",
                breakMinutes = 45,
                isBreakPaid = false,
                overtimeApproved = true
            ),
            AttendanceEntity(
                employeeId = "EMP-104",
                workDate = yesterday.toString(),
                checkInTime = "08:00",
                checkOutTime = "17:00",
                breakMinutes = 45,
                isBreakPaid = false,
                overtimeApproved = true
            ),
            AttendanceEntity(
                employeeId = "EMP-105",
                workDate = yesterday.toString(),
                checkInTime = "08:00",
                checkOutTime = "17:00",
                breakMinutes = 45,
                isBreakPaid = false,
                overtimeApproved = true
            ),
            AttendanceEntity(
                employeeId = "EMP-106",
                workDate = yesterday.toString(),
                checkInTime = "08:00",
                checkOutTime = "17:00",
                breakMinutes = 45,
                isBreakPaid = false,
                overtimeApproved = true
            ),
            AttendanceEntity(
                employeeId = "EMP-107",
                workDate = yesterday.toString(),
                checkInTime = "08:05",
                checkOutTime = "17:00",
                breakMinutes = 45,
                isBreakPaid = false,
                overtimeApproved = true
            ),
            AttendanceEntity(
                employeeId = "EMP-108",
                workDate = yesterday.toString(),
                checkInTime = "08:30",
                checkOutTime = "17:30",
                breakMinutes = 45,
                isBreakPaid = false,
                overtimeApproved = true
            ),

            // Two days ago
            AttendanceEntity(
                employeeId = "EMP-101",
                workDate = twoDaysAgo.toString(),
                checkInTime = "08:00",
                checkOutTime = "17:30",
                breakMinutes = 45,
                isBreakPaid = false,
                overtimeApproved = true
            ),
            AttendanceEntity(
                employeeId = "EMP-102",
                workDate = twoDaysAgo.toString(),
                checkInTime = "08:00",
                checkOutTime = "17:00",
                breakMinutes = 45,
                isBreakPaid = false,
                overtimeApproved = true
            ),
            AttendanceEntity(
                employeeId = "EMP-103",
                workDate = twoDaysAgo.toString(),
                checkInTime = "08:00",
                checkOutTime = "18:00",
                breakMinutes = 45,
                isBreakPaid = false,
                overtimeApproved = true
            ),
            AttendanceEntity(
                employeeId = "EMP-104",
                workDate = twoDaysAgo.toString(),
                checkInTime = "08:00",
                checkOutTime = "17:00",
                breakMinutes = 45,
                isBreakPaid = false,
                overtimeApproved = true
            ),

            // Three days ago
            AttendanceEntity(
                employeeId = "EMP-101",
                workDate = threeDaysAgo.toString(),
                checkInTime = "08:00",
                checkOutTime = "17:00",
                breakMinutes = 45,
                isBreakPaid = false,
                overtimeApproved = true
            ),
            AttendanceEntity(
                employeeId = "EMP-102",
                workDate = threeDaysAgo.toString(),
                checkInTime = "08:00",
                checkOutTime = "17:30",
                breakMinutes = 45,
                isBreakPaid = false,
                overtimeApproved = true
            )
        )
    }

    val INITIAL_LEAVES = listOf(
        LeaveRecordEntity(
            id = 1,
            employeeId = "EMP-107",
            startDate = LocalDate.now().toString(),
            endDate = LocalDate.now().toString(),
            leaveType = "DAILY",
            status = "APPROVED",
            reason = "امور اداری بیمه و تمدید کارت سلامت",
            approvedBy = "مهندس کریمی"
        ),
        LeaveRecordEntity(
            id = 2,
            employeeId = "EMP-106",
            startDate = LocalDate.now().toString(),
            endDate = LocalDate.now().toString(),
            startTime = "16:00",
            endTime = "17:00",
            leaveType = "HOURLY",
            status = "APPROVED",
            reason = "مراجعه به درمانگاه دندانپزشکی",
            approvedBy = "مهندس کریمی"
        ),
        LeaveRecordEntity(
            id = 3,
            employeeId = "EMP-103",
            startDate = LocalDate.now().plusDays(2).toString(),
            endDate = LocalDate.now().plusDays(2).toString(),
            leaveType = "DAILY",
            status = "PENDING",
            reason = "مرخصی استحقاقی خانوادگی",
            approvedBy = "در انتظار بررسی"
        )
    )

    val INITIAL_AUDIT_LOGS = listOf(
        AuditLogEntity(
            timestamp = System.currentTimeMillis() - 7200000,
            actionType = "CREATE",
            targetEntity = "ATTENDANCE",
            description = "ثبت ورود خودکار شیفت صبح کارگاه مبلمان جاینو",
            operatorName = "سیستم خودکار"
        ),
        AuditLogEntity(
            timestamp = System.currentTimeMillis() - 3600000,
            actionType = "UPDATE",
            targetEntity = "LEAVE",
            description = "تایید مرخصی روزانه علیرضا نجفی (EMP-107)",
            operatorName = "مهندس کریمی"
        ),
        AuditLogEntity(
            timestamp = System.currentTimeMillis() - 1800000,
            actionType = "CALCULATION",
            targetEntity = "ATTENDANCE",
            description = "محاسبه کارکرد و اضافه‌کار شیفت دیروز پرسنل سالن نجاری",
            operatorName = "سارا معتمدی"
        )
    )
}
