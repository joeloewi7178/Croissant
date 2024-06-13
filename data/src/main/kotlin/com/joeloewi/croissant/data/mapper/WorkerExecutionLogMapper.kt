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

package com.joeloewi.croissant.data.mapper

import com.joeloewi.croissant.core.database.model.WorkerExecutionLogEntity
import com.joeloewi.croissant.data.mapper.base.Mapper
import com.joeloewi.croissant.domain.entity.WorkerExecutionLog

class WorkerExecutionLogMapper :
    Mapper<WorkerExecutionLog, com.joeloewi.croissant.core.database.model.WorkerExecutionLogEntity> {

    override fun toData(domainEntity: WorkerExecutionLog): com.joeloewi.croissant.core.database.model.WorkerExecutionLogEntity =
        with(domainEntity) {
            com.joeloewi.croissant.core.database.model.WorkerExecutionLogEntity(
                id,
                attendanceId,
                createdAt,
                timezoneId,
                state,
                loggableWorker
            )
        }

    override fun toDomain(dataEntity: com.joeloewi.croissant.core.database.model.WorkerExecutionLogEntity): WorkerExecutionLog =
        with(dataEntity) {
            WorkerExecutionLog(id, attendanceId, createdAt, timezoneId, state, loggableWorker)
        }
}