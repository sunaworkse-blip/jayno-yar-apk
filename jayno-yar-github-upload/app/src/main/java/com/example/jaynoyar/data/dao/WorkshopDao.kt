package com.example.jaynoyar.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.jaynoyar.data.model.AttendanceEntity
import com.example.jaynoyar.data.model.AuditLogEntity
import com.example.jaynoyar.data.model.EmployeeEntity
import com.example.jaynoyar.data.model.LeaveRecordEntity
import com.example.jaynoyar.data.model.WorkScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkshopDao {

    // === Employees ===
    @Query("SELECT * FROM employees ORDER BY firstName ASC, lastName ASC")
    fun getAllEmployeesFlow(): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employees WHERE status != 'ARCHIVED' ORDER BY firstName ASC, lastName ASC")
    fun getActiveEmployeesFlow(): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employees WHERE id = :id LIMIT 1")
    suspend fun getEmployeeById(id: String): EmployeeEntity?

    @Query("SELECT * FROM employees")
    suspend fun getAllEmployees(): List<EmployeeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: EmployeeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployees(employees: List<EmployeeEntity>)

    @Update
    suspend fun updateEmployee(employee: EmployeeEntity)

    @Query("UPDATE employees SET status = :status WHERE id = :id")
    suspend fun updateEmployeeStatus(id: String, status: String)

    // === Attendance ===
    @Query("SELECT * FROM attendances WHERE workDate = :date ORDER BY id ASC")
    fun getAttendancesByDateFlow(date: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendances WHERE workDate = :date")
    suspend fun getAttendancesByDate(date: String): List<AttendanceEntity>

    @Query("SELECT * FROM attendances WHERE employeeId = :employeeId AND workDate = :date LIMIT 1")
    suspend fun getAttendanceByEmployeeAndDate(employeeId: String, date: String): AttendanceEntity?

    @Query("SELECT * FROM attendances WHERE employeeId = :employeeId ORDER BY workDate DESC")
    fun getAttendancesByEmployeeFlow(employeeId: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendances WHERE workDate BETWEEN :startDate AND :endDate ORDER BY workDate ASC")
    suspend fun getAttendancesBetweenDates(startDate: String, endDate: String): List<AttendanceEntity>

    @Query("SELECT * FROM attendances ORDER BY workDate DESC")
    suspend fun getAllAttendances(): List<AttendanceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendances(attendances: List<AttendanceEntity>)

    @Update
    suspend fun updateAttendance(attendance: AttendanceEntity)

    @Query("DELETE FROM attendances WHERE id = :id")
    suspend fun deleteAttendanceById(id: Long)

    @Query("DELETE FROM attendances WHERE workDate = :date AND employeeId = :employeeId")
    suspend fun deleteAttendanceByDateAndEmployee(date: String, employeeId: String)

    // === Work Schedules ===
    @Query("SELECT * FROM work_schedules")
    fun getAllSchedulesFlow(): Flow<List<WorkScheduleEntity>>

    @Query("SELECT * FROM work_schedules WHERE id = :id LIMIT 1")
    suspend fun getScheduleById(id: String): WorkScheduleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: WorkScheduleEntity)

    @Query("SELECT * FROM work_schedules")
    suspend fun getAllSchedules(): List<WorkScheduleEntity>

    // === Leaves ===
    @Query("SELECT * FROM leave_records ORDER BY startDate DESC")
    fun getAllLeavesFlow(): Flow<List<LeaveRecordEntity>>

    @Query("SELECT * FROM leave_records WHERE employeeId = :employeeId AND :date BETWEEN startDate AND endDate LIMIT 1")
    suspend fun getLeaveForEmployeeOnDate(employeeId: String, date: String): LeaveRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeave(leave: LeaveRecordEntity): Long

    @Update
    suspend fun updateLeave(leave: LeaveRecordEntity)

    @Query("UPDATE leave_records SET status = :status WHERE id = :id")
    suspend fun updateLeaveStatus(id: Long, status: String)

    @Query("SELECT * FROM leave_records")
    suspend fun getAllLeaves(): List<LeaveRecordEntity>

    // === Audit Logs ===
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 200")
    fun getRecentAuditLogsFlow(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)

    @Query("SELECT * FROM audit_logs")
    suspend fun getAllAuditLogs(): List<AuditLogEntity>

    // Clear all tables (for database restore)
    @Query("DELETE FROM attendances")
    suspend fun clearAttendances()

    @Query("DELETE FROM employees")
    suspend fun clearEmployees()

    @Query("DELETE FROM leave_records")
    suspend fun clearLeaves()

    @Query("DELETE FROM audit_logs")
    suspend fun clearAuditLogs()
}
