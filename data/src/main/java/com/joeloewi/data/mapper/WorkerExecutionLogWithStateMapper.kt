package com.joeloewi.data.mapper

import com.joeloewi.data.entity.relational.WorkerExecutionLogWithStateEntity
import com.joeloewi.data.mapper.base.ReadOnlyMapper
import com.joeloewi.domain.entity.relational.WorkerExecutionLogWithState

class WorkerExecutionLogWithStateMapper(
    private val workerExecutionLogMapper: WorkerExecutionLogMapper,
    private val successLogMapper: SuccessLogMapper,
    private val failureLogMapper: FailureLogMapper
) : ReadOnlyMapper<WorkerExecutionLogWithState, WorkerExecutionLogWithStateEntity> {
    override fun toDomain(dataEntity: WorkerExecutionLogWithStateEntity): WorkerExecutionLogWithState =
        with(dataEntity) {
            WorkerExecutionLogWithState(
                workerExecutionLog = workerExecutionLogMapper.toDomain(workerExecutionLogEntity),
                successLog = successLogEntity?.let { successLogMapper.toDomain(it) },
                failureLog = failureLogEntity?.let { failureLogMapper.toDomain(it) }
            )
        }
}