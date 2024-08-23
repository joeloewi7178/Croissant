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

package com.joeloewi.croissant.core.data.model

import androidx.compose.runtime.Immutable
import com.joeloewi.croissant.core.database.model.GameEntity

@Immutable
data class Game(
    val id: Long = 0,
    val attendanceId: Long = 0,
    val roleId: Long = 0,
    val type: HoYoLABGame = HoYoLABGame.Unknown,
    val region: String = ""
)

fun GameEntity.asExternalData(): Game = with(this) {
    Game(id, attendanceId, roleId, type.asExternalData(), region)
}

fun Game.asData(): GameEntity = with(this) {
    GameEntity(id, attendanceId, roleId, type.asData(), region)
}