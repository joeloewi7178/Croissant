/*
 *    Copyright 2023. joeloewi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.joeloewi.croissant.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.joeloewi.croissant.data.database.dao.AccountDao
import com.joeloewi.croissant.data.database.dao.AttendanceDao
import com.joeloewi.croissant.data.database.dao.FailureLogDao
import com.joeloewi.croissant.data.database.dao.GameDao
import com.joeloewi.croissant.data.database.dao.ResinStatusWidgetDao
import com.joeloewi.croissant.data.database.dao.SuccessLogDao
import com.joeloewi.croissant.data.database.dao.WorkerExecutionLogDao
import com.joeloewi.croissant.data.entity.local.AccountEntity
import com.joeloewi.croissant.data.entity.local.AttendanceEntity
import com.joeloewi.croissant.data.entity.local.FailureLogEntity
import com.joeloewi.croissant.data.entity.local.GameEntity
import com.joeloewi.croissant.data.entity.local.ResinStatusWidgetEntity
import com.joeloewi.croissant.data.entity.local.SuccessLogEntity
import com.joeloewi.croissant.data.entity.local.WorkerExecutionLogEntity

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