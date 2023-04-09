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

import com.joeloewi.croissant.data.entity.local.relational.WorkerExecutionLogWithStateEntity
import com.joeloewi.croissant.data.mapper.base.ReadOnlyMapper
import com.joeloewi.croissant.domain.entity.relational.WorkerExecutionLogWithState

class WorkerExecutionLogWithStateMapper(
    private val workerExecutionLogMapper: WorkerExecutionLogMapper,
    private val successLogMapper: SuccessLogMapper,
    private val failureLogMapper: FailureLogMapper
) : ReadOnlyMapper<WorkerExecutionLogWithState, WorkerExecutionLogWithStateEntity> {
    override fun toDomain(dataEntity: WorkerExecutionLogWithStateEntity): WorkerExecutionLogWithState =
        with(dataEntity) {
            WorkerExecutionLogWithState(
                workerExecutionLog = workerExecutionLogMapper.toDomain(workerExecutionLogEntity),
                successLog = successLogEntity?.let {
                    successLogMapper.toDomain(it)
                },
                failureLog = failureLogEntity?.let { failureLogMapper.toDomain(it) }
            )
        }
}