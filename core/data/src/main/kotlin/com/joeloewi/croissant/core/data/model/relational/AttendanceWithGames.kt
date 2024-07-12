/*
 *    Copyright 2023. joeloewi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.joeloewi.croissant.core.data.model.relational

import androidx.compose.runtime.Immutable
import com.joeloewi.croissant.core.data.model.Attendance
import com.joeloewi.croissant.core.data.model.Game
import com.joeloewi.croissant.core.data.model.asExternalData
import com.joeloewi.croissant.core.database.model.relational.AttendanceWithGamesEntity

@Immutable
data class AttendanceWithGames(
    val attendance: Attendance = Attendance(),
    val games: List<Game> = listOf()
)

fun AttendanceWithGamesEntity.asExternalData() = with(this) {
    AttendanceWithGames(
        attendance = attendanceEntity.asExternalData(),
        games = gameEntities.map { it.asExternalData() }
    )
}