package com.joeloewi.croissant.data.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class AttendanceWithGames(
    @Embedded val attendance: Attendance = Attendance(),
    @Relation(
        parentColumn = "id",
        entityColumn = "attendanceId"
    )
    val games: List<Game> = listOf()
)
