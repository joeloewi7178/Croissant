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

import com.joeloewi.croissant.core.data.model.GameRecordCardData
import com.joeloewi.croissant.data.entity.remote.GameRecordCardDataEntity
import com.joeloewi.croissant.data.mapper.base.ReadOnlyMapper

class GameRecordCardDataMapper(
    private val gameRecordMapper: GameRecordMapper
) : ReadOnlyMapper<com.joeloewi.croissant.core.data.model.GameRecordCardData, GameRecordCardDataEntity> {
    override fun toDomain(dataEntity: GameRecordCardDataEntity): com.joeloewi.croissant.core.data.model.GameRecordCardData =
        with(dataEntity) {
            com.joeloewi.croissant.core.data.model.GameRecordCardData(list.map {
                gameRecordMapper.toDomain(
                    it
                )
            })
        }
}