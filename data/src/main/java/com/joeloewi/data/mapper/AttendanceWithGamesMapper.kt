package com.joeloewi.data.mapper

import com.joeloewi.data.entity.relational.AttendanceWithGamesEntity
import com.joeloewi.data.mapper.base.ReadOnlyMapper
import com.joeloewi.domain.entity.relational.AttendanceWithGames

class AttendanceWithGamesMapper(
    private val attendanceMapper: AttendanceMapper,
    private val gameMapper: GameMapper
) : ReadOnlyMapper<AttendanceWithGames, AttendanceWithGamesEntity> {

    override fun toDomain(dataEntity: AttendanceWithGamesEntity): AttendanceWithGames =
        with(dataEntity) {
            AttendanceWithGames(
                attendance = attendanceMapper.toDomain(attendanceEntity),
                games = gameEntities.map { gameMapper.toDomain(it) }
            )
        }
}