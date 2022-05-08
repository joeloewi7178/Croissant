package com.joeloewi.data.entity.relational

import androidx.room.Embedded
import androidx.room.Relation
import com.joeloewi.data.entity.AttendanceEntity
import com.joeloewi.data.entity.GameEntity

data class AttendanceWithGamesEntity(
    @Embedded val attendanceEntity: AttendanceEntity = AttendanceEntity(),
    @Relation(
        parentColumn = "id",
        entityColumn = "attendanceId"
    ) val gameEntities: List<GameEntity> = listOf()
)
