package com.joeloewi.croissant.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.joeloewi.croissant.data.local.dao.AttendanceDao
import com.joeloewi.croissant.data.local.dao.ExecutionLogDao
import com.joeloewi.croissant.data.local.dao.GameDao
import com.joeloewi.croissant.data.local.dao.ReminderDao
import com.joeloewi.croissant.data.local.model.*

@Database(
    entities = [
        Attendance::class,
        ExecutionLog::class,
        Game::class,
        Reminder::class,
        ScheduledDay::class
    ],
    exportSchema = true,
    version = CroissantDatabase.LATEST_VERSION
)
abstract class CroissantDatabase : RoomDatabase() {
    abstract fun attendanceDao(): AttendanceDao
    abstract fun executionLogDao(): ExecutionLogDao
    abstract fun gameDao(): GameDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        const val LATEST_VERSION = 1
    }
}