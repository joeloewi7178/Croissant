package com.joeloewi.domain.usecase

import com.joeloewi.domain.entity.Attendance
import com.joeloewi.domain.repository.AttendanceRepository
import javax.inject.Inject

sealed class AttendanceUseCase {
    class Insert @Inject constructor(
        private val attendanceRepository: AttendanceRepository
    ) : AttendanceUseCase() {
        suspend operator fun invoke(attendance: Attendance) =
            attendanceRepository.insert(attendance)
    }

    class Update @Inject constructor(
        private val attendanceRepository: AttendanceRepository
    ) : AttendanceUseCase() {
        suspend operator fun invoke(vararg attendances: Attendance) =
            attendanceRepository.update(*attendances)
    }

    class Delete @Inject constructor(
        private val attendanceRepository: AttendanceRepository
    ) : AttendanceUseCase() {
        suspend operator fun invoke(vararg attendances: Attendance) =
            attendanceRepository.delete(*attendances)
    }

    class GetOne @Inject constructor(
        private val attendanceRepository: AttendanceRepository
    ) : AttendanceUseCase() {
        suspend operator fun invoke(id: Long) = attendanceRepository.getOne(id)
    }

    class GetOneByUid @Inject constructor(
        private val attendanceRepository: AttendanceRepository
    ) : AttendanceUseCase() {
        suspend operator fun invoke(uid: Long) = attendanceRepository.getOneByUid(uid)
    }

    class GetByIds @Inject constructor(
        private val attendanceRepository: AttendanceRepository
    ) : AttendanceUseCase() {
        suspend operator fun invoke(vararg ids: Long) = attendanceRepository.getByIds(*ids)
    }

    class GetAllPaged @Inject constructor(
        private val attendanceRepository: AttendanceRepository
    ) : AttendanceUseCase() {
        operator fun invoke() = attendanceRepository.getAllPaged()
    }
}


