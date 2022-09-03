package com.joeloewi.data.mapper

import com.joeloewi.data.entity.local.GameEntity
import com.joeloewi.data.mapper.base.Mapper
import com.joeloewi.domain.entity.Game

class GameMapper : Mapper<Game, GameEntity> {
    override fun toData(domainEntity: Game): GameEntity = with(domainEntity) {
        GameEntity(id, attendanceId, roleId, type, region)
    }

    override fun toDomain(dataEntity: GameEntity): Game = with(dataEntity) {
        Game(id, attendanceId, roleId, type, region)
    }
}