package com.joeloewi.data.mapper

import com.joeloewi.data.entity.ResinStatusWidgetEntity
import com.joeloewi.data.mapper.base.Mapper
import com.joeloewi.domain.entity.ResinStatusWidget

class ResinStatusWidgetMapper : Mapper<ResinStatusWidget, ResinStatusWidgetEntity> {
    override fun toData(domainEntity: ResinStatusWidget): ResinStatusWidgetEntity =
        with(domainEntity) {
            ResinStatusWidgetEntity(id, appWidgetId, interval, refreshGenshinResinStatusWorkerName)
        }

    override fun toDomain(dataEntity: ResinStatusWidgetEntity): ResinStatusWidget =
        with(dataEntity) {
            ResinStatusWidget(id, appWidgetId, interval, refreshGenshinResinStatusWorkerName)
        }
}