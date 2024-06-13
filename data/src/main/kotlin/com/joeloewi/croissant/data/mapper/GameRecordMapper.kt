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

package com.joeloewi.croissant.data.mapper

import com.joeloewi.croissant.core.data.model.GameRecord
import com.joeloewi.croissant.data.entity.remote.GameRecordEntity
import com.joeloewi.croissant.data.mapper.base.ReadOnlyMapper

class GameRecordMapper(
    private val dataSwitchMapper: DataSwitchMapper
) : ReadOnlyMapper<com.joeloewi.croissant.core.data.model.GameRecord, GameRecordEntity> {
    override fun toDomain(dataEntity: GameRecordEntity): com.joeloewi.croissant.core.data.model.GameRecord =
        with(dataEntity) {
            com.joeloewi.croissant.core.data.model.GameRecord(
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