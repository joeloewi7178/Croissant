package com.joeloewi.croissant.data.local.model.relational

import androidx.room.Embedded
import androidx.room.Relation
import com.joeloewi.croissant.data.local.model.Attendance
import com.joeloewi.croissant.data.local.model.Game

data class AttendanceWithGames(
    @Embedded val attendance: Attendance = Attendance(),
    @Relation(
        parentColumn = "id",
        entityColumn = "attendanceId"
    )
    val games: List<Game> = listOf()
)
