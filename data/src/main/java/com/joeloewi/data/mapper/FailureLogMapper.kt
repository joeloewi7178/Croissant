package com.joeloewi.data.mapper

import com.joeloewi.data.entity.FailureLogEntity
import com.joeloewi.data.mapper.base.Mapper
import com.joeloewi.domain.entity.FailureLog

class FailureLogMapper : Mapper<FailureLog, FailureLogEntity> {

    override fun toData(domainEntity: FailureLog): FailureLogEntity = with(domainEntity) {
        FailureLogEntity(id, executionLogId, failureMessage, failureStackTrace)
    }

    override fun toDomain(dataEntity: FailureLogEntity): FailureLog = with(dataEntity) {
        FailureLog(id, executionLogId, failureMessage, failureStackTrace)
    }
}