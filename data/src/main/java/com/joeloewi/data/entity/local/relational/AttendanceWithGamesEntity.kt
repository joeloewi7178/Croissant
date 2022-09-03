package com.joeloewi.data.entity.local.relational

import androidx.room.Embedded
import androidx.room.Relation
import com.joeloewi.data.entity.local.AttendanceEntity
import com.joeloewi.data.entity.local.GameEntity

data class AttendanceWithGamesEntity(
    @Embedded val attendanceEntity: AttendanceEntity = AttendanceEntity(),
    @Relation(
        parentColumn = "id",
        entityColumn = "attendanceId"
    ) val gameEntities: List<GameEntity> = listOf()
)
