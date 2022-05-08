package com.joeloewi.domain.entity.relational

import com.joeloewi.domain.entity.Attendance
import com.joeloewi.domain.entity.Game

data class AttendanceWithGames(
    val attendance: Attendance = Attendance(),
    val games: List<Game> = listOf()
)