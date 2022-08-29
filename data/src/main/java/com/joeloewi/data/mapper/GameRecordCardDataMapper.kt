package com.joeloewi.data.mapper

import com.joeloewi.data.entity.remote.GameRecordCardDataEntity
import com.joeloewi.data.mapper.base.ReadOnlyMapper
import com.joeloewi.domain.entity.GameRecordCardData

class GameRecordCardDataMapper(
    private val gameRecordMapper: GameRecordMapper
) : ReadOnlyMapper<GameRecordCardData, GameRecordCardDataEntity> {
    override fun toDomain(dataEntity: GameRecordCardDataEntity): GameRecordCardData =
        with(dataEntity) {
            GameRecordCardData(list.map { gameRecordMapper.toDomain(it) })
        }
}