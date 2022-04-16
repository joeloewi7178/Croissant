package com.joeloewi.croissant.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.joeloewi.croissant.data.local.dao.*
import com.joeloewi.croissant.data.local.model.*

@Database(
    entities = [
        Attendance::class,
        WorkerExecutionLog::class,
        Game::class,
        Reminder::class,
        ScheduledDay::class,
        SuccessLog::class,
        FailureLog::class
    ],
    exportSchema = true,
    version = CroissantDatabase.LATEST_VERSION
)
abstract class CroissantDatabase : RoomDatabase() {
    abstract fun attendanceDao(): AttendanceDao
    abstract fun workerExecutionLogDao(): WorkerExecutionLogDao
    abstract fun gameDao(): GameDao
    abstract fun reminderDao(): ReminderDao
    abstract fun successLogDao(): SuccessLogDao
    abstract fun failureLogDao(): FailureLogDao

    companion object {
        const val LATEST_VERSION = 1
    }
}