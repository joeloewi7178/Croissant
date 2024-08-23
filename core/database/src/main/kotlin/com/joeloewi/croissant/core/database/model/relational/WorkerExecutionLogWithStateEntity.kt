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

package com.joeloewi.croissant.core.database.model.relational

import androidx.room.Embedded
import androidx.room.Relation
import com.joeloewi.croissant.core.database.model.FailureLogEntity
import com.joeloewi.croissant.core.database.model.SuccessLogEntity
import com.joeloewi.croissant.core.database.model.WorkerExecutionLogEntity

data class WorkerExecutionLogWithStateEntity(
    @Embedded val workerExecutionLogEntity: WorkerExecutionLogEntity = WorkerExecutionLogEntity(),
    @Relation(
        parentColumn = "id",
        entityColumn = "executionLogId"
    ) val successLogEntity: SuccessLogEntity? = null,
    @Relation(
        parentColumn = "id",
        entityColumn = "executionLogId"
    ) val failureLogEntity: FailureLogEntity? = null,
)