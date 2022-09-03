package com.joeloewi.data.mapper

import com.joeloewi.data.entity.remote.DataSwitchEntity
import com.joeloewi.data.mapper.base.ReadOnlyMapper
import com.joeloewi.domain.entity.DataSwitch

class DataSwitchMapper : ReadOnlyMapper<DataSwitch, DataSwitchEntity> {
    override fun toDomain(dataEntity: DataSwitchEntity): DataSwitch = with(dataEntity) {
        DataSwitch(switchId, isPublic, switchName)
    }
}