package com.joeloewi.data.mapper

import com.joeloewi.data.entity.remote.GameRecordEntity
import com.joeloewi.data.mapper.base.ReadOnlyMapper
import com.joeloewi.domain.entity.GameRecord

class GameRecordMapper(
    private val dataSwitchMapper: DataSwitchMapper
) : ReadOnlyMapper<GameRecord, GameRecordEntity> {
    override fun toDomain(dataEntity: GameRecordEntity): GameRecord = with(dataEntity) {
        GameRecord(
            hasRole,
            gameId,
            gameRoleId,
            nickname,
            level,
            regionName,
            region,
            dataSwitches.map { dataSwitchMapper.toDomain(it) })
    }
}