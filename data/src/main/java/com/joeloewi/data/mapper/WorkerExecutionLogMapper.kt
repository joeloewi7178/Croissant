package com.joeloewi.data.mapper

import com.joeloewi.data.entity.WorkerExecutionLogEntity
import com.joeloewi.data.mapper.base.Mapper
import com.joeloewi.domain.entity.WorkerExecutionLog

class WorkerExecutionLogMapper : Mapper<WorkerExecutionLog, WorkerExecutionLogEntity> {

    override fun toData(domainEntity: WorkerExecutionLog): WorkerExecutionLogEntity =
        with(domainEntity) {
            WorkerExecutionLogEntity(id, attendanceId, createdAt, timezoneId, state, loggableWorker)
        }

    override fun toDomain(dataEntity: WorkerExecutionLogEntity): WorkerExecutionLog =
        with(dataEntity) {
            WorkerExecutionLog(id, attendanceId, createdAt, timezoneId, state, loggableWorker)
        }
}