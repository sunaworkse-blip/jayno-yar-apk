package com.example.jaynoyar.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.jaynoyar.data.dao.WorkshopDao
import com.example.jaynoyar.data.model.AttendanceEntity
import com.example.jaynoyar.data.model.AuditLogEntity
import com.example.jaynoyar.data.model.EmployeeEntity
import com.example.jaynoyar.data.model.LeaveRecordEntity
import com.example.jaynoyar.data.model.WorkScheduleEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        EmployeeEntity::class,
        AttendanceEntity::class,
        WorkScheduleEntity::class,
        LeaveRecordEntity::class,
        AuditLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WorkshopDatabase : RoomDatabase() {

    abstract fun workshopDao(): WorkshopDao

    companion object {
        @Volatile
        private var INSTANCE: WorkshopDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): WorkshopDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkshopDatabase::class.java,
                    "jayno_yar_workshop.db"
                )
                    .addCallback(WorkshopDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class WorkshopDatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.workshopDao())
                    }
                }
            }

            suspend fun populateDatabase(dao: WorkshopDao) {
                dao.insertSchedule(SeedData.DEFAULT_SCHEDULE)
                dao.insertEmployees(SeedData.INITIAL_EMPLOYEES)
                dao.insertAttendances(SeedData.createInitialAttendances())
                SeedData.INITIAL_LEAVES.forEach { dao.insertLeave(it) }
                SeedData.INITIAL_AUDIT_LOGS.forEach { dao.insertAuditLog(it) }
            }
        }
    }
}
