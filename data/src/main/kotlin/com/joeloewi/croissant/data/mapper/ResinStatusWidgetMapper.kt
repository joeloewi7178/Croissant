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

import com.joeloewi.croissant.core.database.model.ResinStatusWidgetEntity
import com.joeloewi.croissant.data.mapper.base.Mapper
import com.joeloewi.croissant.domain.entity.ResinStatusWidget

class ResinStatusWidgetMapper :
    Mapper<ResinStatusWidget, com.joeloewi.croissant.core.database.model.ResinStatusWidgetEntity> {
    override fun toData(domainEntity: ResinStatusWidget): com.joeloewi.croissant.core.database.model.ResinStatusWidgetEntity =
        with(domainEntity) {
            com.joeloewi.croissant.core.database.model.ResinStatusWidgetEntity(
                id,
                appWidgetId,
                interval,
                refreshGenshinResinStatusWorkerName
            )
        }

    override fun toDomain(dataEntity: com.joeloewi.croissant.core.database.model.ResinStatusWidgetEntity): ResinStatusWidget =
        with(dataEntity) {
            ResinStatusWidget(id, appWidgetId, interval, refreshGenshinResinStatusWorkerName)
        }
}