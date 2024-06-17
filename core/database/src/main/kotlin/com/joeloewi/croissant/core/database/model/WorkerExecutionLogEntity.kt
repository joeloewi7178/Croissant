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

package com.joeloewi.croissant.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.TimeZone

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = AttendanceEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("attendanceId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WorkerExecutionLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(index = true)
    val attendanceId: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val timezoneId: String = TimeZone.getDefault().id,
    @ColumnInfo(index = true)
    val state: DataWorkerExecutionLogState = DataWorkerExecutionLogState.SUCCESS,
    @ColumnInfo(index = true)
    val loggableWorker: DataLoggableWorker = DataLoggableWorker.UNKNOWN,
)
