package com.joeloewi.domain.usecase

import com.joeloewi.domain.entity.Attendance
import com.joeloewi.domain.repository.AttendanceRepository

sealed class AttendanceUseCase {
    class Insert constructor(
        private val attendanceRepository: AttendanceRepository
    ) : AttendanceUseCase() {
        suspend operator fun invoke(attendance: Attendance) =
            attendanceRepository.insert(attendance)
    }

    class Update constructor(
        private val attendanceRepository: AttendanceRepository
    ) : AttendanceUseCase() {
        suspend operator fun invoke(vararg attendances: Attendance) =
            attendanceRepository.update(*attendances)
    }

    class Delete constructor(
        private val attendanceRepository: AttendanceRepository
    ) : AttendanceUseCase() {
        suspend operator fun invoke(vararg attendances: Attendance) =
            attendanceRepository.delete(*attendances)
    }

    class GetOne constructor(
        private val attendanceRepository: AttendanceRepository
    ) : AttendanceUseCase() {
        suspend operator fun invoke(id: Long) = attendanceRepository.getOne(id)
    }

    class GetOneByUid constructor(
        private val attendanceRepository: AttendanceRepository
    ) : AttendanceUseCase() {
        suspend operator fun invoke(uid: Long) = attendanceRepository.getOneByUid(uid)
    }

    class GetByIds constructor(
        private val attendanceRepository: AttendanceRepository
    ) : AttendanceUseCase() {
        suspend operator fun invoke(vararg ids: Long) = attendanceRepository.getByIds(*ids)
    }

    class GetAllPaged constructor(
        private val attendanceRepository: AttendanceRepository
    ) : AttendanceUseCase() {
        operator fun invoke() = attendanceRepository.getAllPaged()
    }
}


