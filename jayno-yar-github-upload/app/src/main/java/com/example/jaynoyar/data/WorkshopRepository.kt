package com.example.jaynoyar.data

import com.example.jaynoyar.calculator.AttendanceCalculationBreakdown
import com.example.jaynoyar.calculator.AttendanceCalculator
import com.example.jaynoyar.data.dao.WorkshopDao
import com.example.jaynoyar.data.model.AttendanceEntity
import com.example.jaynoyar.data.model.AuditLogEntity
import com.example.jaynoyar.data.model.EmployeeEntity
import com.example.jaynoyar.data.model.LeaveRecordEntity
import com.example.jaynoyar.data.model.WorkScheduleEntity
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

class WorkshopRepository(private val dao: WorkshopDao) {

    val allEmployees: Flow<List<EmployeeEntity>> = dao.getAllEmployeesFlow()
    val activeEmployees: Flow<List<EmployeeEntity>> = dao.getActiveEmployeesFlow()
    val allSchedules: Flow<List<WorkScheduleEntity>> = dao.getAllSchedulesFlow()
    val allLeaves: Flow<List<LeaveRecordEntity>> = dao.getAllLeavesFlow()
    val recentAuditLogs: Flow<List<AuditLogEntity>> = dao.getRecentAuditLogsFlow()

    fun getAttendancesForDateFlow(date: String): Flow<List<AttendanceEntity>> {
        return dao.getAttendancesByDateFlow(date)
    }

    suspend fun getAttendancesForDate(date: String): List<AttendanceEntity> {
        return dao.getAttendancesByDate(date)
    }

    suspend fun getAttendancesBetweenDates(start: String, end: String): List<AttendanceEntity> {
        return dao.getAttendancesBetweenDates(start, end)
    }

    suspend fun getAllEmployees(): List<EmployeeEntity> {
        return dao.getAllEmployees()
    }

    suspend fun getEmployee(id: String): EmployeeEntity? {
        return dao.getEmployeeById(id)
    }

    suspend fun saveEmployee(employee: EmployeeEntity) {
        dao.insertEmployee(employee)
        dao.insertAuditLog(
            AuditLogEntity(
                actionType = "UPDATE",
                targetEntity = "EMPLOYEE",
                description = "ثبت/ویرایش اطلاعات پرسنل: ${employee.fullName} (${employee.id})"
            )
        )
    }

    suspend fun setEmployeeStatus(id: String, status: String) {
        dao.updateEmployeeStatus(id, status)
        dao.insertAuditLog(
            AuditLogEntity(
                actionType = "UPDATE",
                targetEntity = "EMPLOYEE",
                description = "تغییر وضعیت پرسنل $id به $status"
            )
        )
    }

    suspend fun recordAttendance(attendance: AttendanceEntity): Long {
        val existing = dao.getAttendanceByEmployeeAndDate(attendance.employeeId, attendance.workDate)
        val toSave = if (existing != null && attendance.id == 0L) {
            attendance.copy(id = existing.id)
        } else {
            attendance
        }
        val id = dao.insertAttendance(toSave)
        dao.insertAuditLog(
            AuditLogEntity(
                actionType = "UPDATE",
                targetEntity = "ATTENDANCE",
                description = "ثبت تردد پرسنل ${attendance.employeeId} در تاریخ ${attendance.workDate} (ورود: ${attendance.checkInTime ?: "-"}، خروج: ${attendance.checkOutTime ?: "-"})"
            )
        )
        return id
    }

    suspend fun saveAttendances(attendances: List<AttendanceEntity>, logDescription: String) {
        val resolved = attendances.map { att ->
            if (att.id == 0L) {
                val existing = dao.getAttendanceByEmployeeAndDate(att.employeeId, att.workDate)
                if (existing != null) att.copy(id = existing.id) else att
            } else {
                att
            }
        }
        dao.insertAttendances(resolved)
        dao.insertAuditLog(
            AuditLogEntity(
                actionType = "IMPORT",
                targetEntity = "ATTENDANCE",
                description = logDescription
            )
        )
    }

    suspend fun deleteAttendance(id: Long, employeeId: String, date: String) {
        dao.deleteAttendanceById(id)
        dao.insertAuditLog(
            AuditLogEntity(
                actionType = "DELETE",
                targetEntity = "ATTENDANCE",
                description = "حذف رکورد تردد $employeeId در تاریخ $date"
            )
        )
    }

    suspend fun getSchedule(scheduleId: String = "DEFAULT_WORKSHOP"): WorkScheduleEntity {
        return dao.getScheduleById(scheduleId) ?: SeedData.DEFAULT_SCHEDULE
    }

    suspend fun saveSchedule(schedule: WorkScheduleEntity) {
        dao.insertSchedule(schedule)
        dao.insertAuditLog(
            AuditLogEntity(
                actionType = "UPDATE",
                targetEntity = "SCHEDULE",
                description = "بروزرسانی قوانین برنامه کاری کارگاه: ${schedule.title}"
            )
        )
    }

    suspend fun submitLeave(leave: LeaveRecordEntity): Long {
        val id = dao.insertLeave(leave)
        dao.insertAuditLog(
            AuditLogEntity(
                actionType = "CREATE",
                targetEntity = "LEAVE",
                description = "ثبت درخواست مرخصی برای ${leave.employeeId} از ${leave.startDate} تا ${leave.endDate}"
            )
        )
        return id
    }

    suspend fun updateLeaveStatus(id: Long, status: String, employeeId: String) {
        dao.updateLeaveStatus(id, status)
        dao.insertAuditLog(
            AuditLogEntity(
                actionType = "UPDATE",
                targetEntity = "LEAVE",
                description = "تغییر وضعیت مرخصی #$id پرسنل $employeeId به $status"
            )
        )
    }

    suspend fun getLeaveForEmployeeOnDate(employeeId: String, date: String): LeaveRecordEntity? {
        return dao.getLeaveForEmployeeOnDate(employeeId, date)
    }

    suspend fun calculateBreakdownForEmployeeDate(
        employeeId: String,
        date: String,
        attendance: AttendanceEntity? = null
    ): AttendanceCalculationBreakdown {
        val employee = dao.getEmployeeById(employeeId)
        val schedule = dao.getScheduleById(employee?.scheduleId ?: "DEFAULT_WORKSHOP") ?: SeedData.DEFAULT_SCHEDULE
        val leave = dao.getLeaveForEmployeeOnDate(employeeId, date)

        val att = attendance ?: dao.getAttendancesByDate(date).find { it.employeeId == employeeId }
        ?: AttendanceEntity(
            employeeId = employeeId,
            workDate = date,
            checkInTime = null,
            checkOutTime = null,
            breakMinutes = schedule.lunchBreakMinutes
        )

        return AttendanceCalculator.computeBreakdown(
            attendance = att,
            schedule = schedule,
            isHoliday = false,
            leaveRecord = leave
        )
    }

    /**
     * Export complete database to JSON for reliable backup
     */
    suspend fun exportDatabaseBackupJson(): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("workshop", "کارگاه مبلمان جاینو")
        root.put("timestamp", System.currentTimeMillis())

        val empArray = JSONArray()
        dao.getAllEmployees().forEach { e ->
            val o = JSONObject()
            o.put("id", e.id)
            o.put("firstName", e.firstName)
            o.put("lastName", e.lastName)
            o.put("department", e.department)
            o.put("role", e.role)
            o.put("nationalId", e.nationalId)
            o.put("phone", e.phone)
            o.put("hireDate", e.hireDate)
            o.put("status", e.status)
            o.put("baseHourlyWage", e.baseHourlyWage)
            o.put("scheduleId", e.scheduleId)
            o.put("notes", e.notes)
            empArray.put(o)
        }
        root.put("employees", empArray)

        val attArray = JSONArray()
        dao.getAllAttendances().forEach { a ->
            val o = JSONObject()
            o.put("employeeId", a.employeeId)
            o.put("workDate", a.workDate)
            o.put("checkInTime", a.checkInTime ?: JSONObject.NULL)
            o.put("checkOutTime", a.checkOutTime ?: JSONObject.NULL)
            o.put("breakMinutes", a.breakMinutes)
            o.put("isBreakPaid", a.isBreakPaid)
            o.put("isOvernight", a.isOvernight)
            o.put("overtimeApproved", a.overtimeApproved)
            o.put("note", a.note ?: JSONObject.NULL)
            attArray.put(o)
        }
        root.put("attendances", attArray)

        val leavesArray = JSONArray()
        dao.getAllLeaves().forEach { l ->
            val o = JSONObject()
            o.put("employeeId", l.employeeId)
            o.put("startDate", l.startDate)
            o.put("endDate", l.endDate)
            o.put("startTime", l.startTime ?: JSONObject.NULL)
            o.put("endTime", l.endTime ?: JSONObject.NULL)
            o.put("leaveType", l.leaveType)
            o.put("status", l.status)
            o.put("reason", l.reason)
            o.put("approvedBy", l.approvedBy)
            leavesArray.put(o)
        }
        root.put("leaves", leavesArray)

        return root.toString(2)
    }

    /**
     * Restore database from JSON backup
     */
    suspend fun restoreDatabaseFromJson(jsonStr: String): Boolean {
        return try {
            val root = JSONObject(jsonStr)
            val empArray = root.optJSONArray("employees") ?: JSONArray()
            val attArray = root.optJSONArray("attendances") ?: JSONArray()
            val leavesArray = root.optJSONArray("leaves") ?: JSONArray()

            val employees = mutableListOf<EmployeeEntity>()
            for (i in 0 until empArray.length()) {
                val o = empArray.getJSONObject(i)
                employees.add(
                    EmployeeEntity(
                        id = o.getString("id"),
                        firstName = o.getString("firstName"),
                        lastName = o.getString("lastName"),
                        department = o.getString("department"),
                        role = o.getString("role"),
                        nationalId = o.optString("nationalId", ""),
                        phone = o.optString("phone", ""),
                        hireDate = o.optString("hireDate", "2024-01-01"),
                        status = o.optString("status", "ACTIVE"),
                        baseHourlyWage = o.optLong("baseHourlyWage", 100000),
                        scheduleId = o.optString("scheduleId", "DEFAULT_WORKSHOP"),
                        notes = o.optString("notes", "")
                    )
                )
            }

            val attendances = mutableListOf<AttendanceEntity>()
            for (i in 0 until attArray.length()) {
                val o = attArray.getJSONObject(i)
                attendances.add(
                    AttendanceEntity(
                        employeeId = o.getString("employeeId"),
                        workDate = o.getString("workDate"),
                        checkInTime = if (o.isNull("checkInTime")) null else o.optString("checkInTime"),
                        checkOutTime = if (o.isNull("checkOutTime")) null else o.optString("checkOutTime"),
                        breakMinutes = o.optInt("breakMinutes", 45),
                        isBreakPaid = o.optBoolean("isBreakPaid", false),
                        isOvernight = o.optBoolean("isOvernight", false),
                        overtimeApproved = o.optBoolean("overtimeApproved", true),
                        note = if (o.isNull("note")) null else o.optString("note")
                    )
                )
            }

            val leaves = mutableListOf<LeaveRecordEntity>()
            for (i in 0 until leavesArray.length()) {
                val o = leavesArray.getJSONObject(i)
                leaves.add(
                    LeaveRecordEntity(
                        employeeId = o.getString("employeeId"),
                        startDate = o.getString("startDate"),
                        endDate = o.getString("endDate"),
                        startTime = if (o.isNull("startTime")) null else o.optString("startTime"),
                        endTime = if (o.isNull("endTime")) null else o.optString("endTime"),
                        leaveType = o.getString("leaveType"),
                        status = o.getString("status"),
                        reason = o.optString("reason", ""),
                        approvedBy = o.optString("approvedBy", "مدیر کارگاه")
                    )
                )
            }

            dao.clearAttendances()
            dao.clearEmployees()
            dao.clearLeaves()

            dao.insertEmployees(employees)
            dao.insertAttendances(attendances)
            leaves.forEach { dao.insertLeave(it) }

            dao.insertAuditLog(
                AuditLogEntity(
                    actionType = "RESTORE",
                    targetEntity = "ATTENDANCE",
                    description = "بازیابی موفق پایگاه داده از فایل پشتیبان (${employees.size} پرسنل، ${attendances.size} تردد، ${leaves.size} مرخصی)"
                )
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun resetToWorkshopDemoData() {
        dao.clearAttendances()
        dao.clearEmployees()
        dao.clearLeaves()
        dao.clearAuditLogs()

        dao.insertSchedule(SeedData.DEFAULT_SCHEDULE)
        dao.insertEmployees(SeedData.INITIAL_EMPLOYEES)
        dao.insertAttendances(SeedData.createInitialAttendances())
        SeedData.INITIAL_LEAVES.forEach { dao.insertLeave(it) }
        SeedData.INITIAL_AUDIT_LOGS.forEach { dao.insertAuditLog(it) }
        dao.insertAuditLog(
            AuditLogEntity(
                actionType = "RESTORE",
                targetEntity = "ATTENDANCE",
                description = "بازنشانی داده‌های نمونه کارگاه مبلمان جاینو به حالت اولیه"
            )
        )
    }
}
