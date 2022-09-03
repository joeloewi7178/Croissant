package com.joeloewi.data.mapper

import com.joeloewi.data.entity.remote.GenshinDailyNoteDataEntity
import com.joeloewi.data.mapper.base.ReadOnlyMapper
import com.joeloewi.domain.entity.GenshinDailyNoteData

class GenshinDailyNoteDataMapper :
    ReadOnlyMapper<GenshinDailyNoteData, GenshinDailyNoteDataEntity> {
    override fun toDomain(dataEntity: GenshinDailyNoteDataEntity): GenshinDailyNoteData =
        with(dataEntity) {
            GenshinDailyNoteData(currentResin, maxResin)
        }
}