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

import com.joeloewi.croissant.core.database.model.AttendanceEntity
import com.joeloewi.croissant.data.mapper.base.Mapper
import com.joeloewi.croissant.domain.entity.Attendance

class AttendanceMapper :
    Mapper<Attendance, com.joeloewi.croissant.core.database.model.AttendanceEntity> {
    override fun toData(domainEntity: Attendance): com.joeloewi.croissant.core.database.model.AttendanceEntity =
        with(domainEntity) {
            com.joeloewi.croissant.core.database.model.AttendanceEntity(
                id,
                createdAt,
                modifiedAt,
                cookie,
                nickname,
                uid,
                hourOfDay,
                minute,
                timezoneId,
                attendCheckInEventWorkerName,
                attendCheckInEventWorkerId,
                checkSessionWorkerName,
                checkSessionWorkerId,
                oneTimeAttendCheckInEventWorkerName
            )
        }

    override fun toDomain(dataEntity: com.joeloewi.croissant.core.database.model.AttendanceEntity): Attendance =
        with(dataEntity) {
            Attendance(
                id,
                createdAt,
                modifiedAt,
                cookie,
                nickname,
                uid,
                hourOfDay,
                minute,
                timezoneId,
                attendCheckInEventWorkerName,
                attendCheckInEventWorkerId,
                checkSessionWorkerName,
                checkSessionWorkerId,
                oneTimeAttendCheckInEventWorkerName
            )
        }
}