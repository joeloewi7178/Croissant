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

package com.joeloewi.croissant.domain.usecase

import com.joeloewi.croissant.domain.entity.Attendance
import com.joeloewi.croissant.domain.repository.AttendanceRepository
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

    class GetAllOneShot @Inject constructor(
        private val attendanceRepository: AttendanceRepository
    ) : AttendanceUseCase() {
        suspend operator fun invoke() = attendanceRepository.getAllOneShot()
    }
}


