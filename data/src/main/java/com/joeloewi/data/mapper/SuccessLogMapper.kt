package com.joeloewi.data.mapper

import com.joeloewi.data.entity.local.SuccessLogEntity
import com.joeloewi.data.mapper.base.Mapper
import com.joeloewi.domain.entity.SuccessLog

class SuccessLogMapper : Mapper<SuccessLog, SuccessLogEntity> {
    override fun toData(domainEntity: SuccessLog): SuccessLogEntity = with(domainEntity) {
        SuccessLogEntity(id, executionLogId, gameName, retCode, message)
    }

    override fun toDomain(dataEntity: SuccessLogEntity): SuccessLog = with(dataEntity) {
        SuccessLog(id, executionLogId, gameName, retCode, message)
    }
}