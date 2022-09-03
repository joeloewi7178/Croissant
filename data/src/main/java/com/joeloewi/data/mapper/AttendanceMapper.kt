package com.joeloewi.data.mapper

import com.joeloewi.data.entity.local.AttendanceEntity
import com.joeloewi.data.mapper.base.Mapper
import com.joeloewi.domain.entity.Attendance

class AttendanceMapper : Mapper<Attendance, AttendanceEntity> {
    override fun toData(domainEntity: Attendance): AttendanceEntity = with(domainEntity) {
        AttendanceEntity(
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

    override fun toDomain(dataEntity: AttendanceEntity): Attendance = with(dataEntity) {
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