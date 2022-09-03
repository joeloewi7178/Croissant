package com.joeloewi.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.joeloewi.data.db.dao.*
import com.joeloewi.data.entity.local.*

@Database(
    entities = [
        AttendanceEntity::class,
        WorkerExecutionLogEntity::class,
        GameEntity::class,
        SuccessLogEntity::class,
        FailureLogEntity::class,
        ResinStatusWidgetEntity::class,
        AccountEntity::class
    ],
    exportSchema = true,
    version = CroissantDatabase.LATEST_VERSION
)
abstract class CroissantDatabase : RoomDatabase() {
    abstract fun attendanceDao(): AttendanceDao
    abstract fun workerExecutionLogDao(): WorkerExecutionLogDao
    abstract fun gameDao(): GameDao
    abstract fun successLogDao(): SuccessLogDao
    abstract fun failureLogDao(): FailureLogDao
    abstract fun resinStatusWidgetDao(): ResinStatusWidgetDao
    abstract fun accountDao(): AccountDao

    companion object {
        const val LATEST_VERSION = 1
    }
}