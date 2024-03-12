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

package com.joeloewi.croissant.data.di

import com.joeloewi.croissant.data.repository.AccountRepositoryImpl
import com.joeloewi.croissant.data.repository.ArcaLiveAppRepositoryImpl
import com.joeloewi.croissant.data.repository.AttendanceRepositoryImpl
import com.joeloewi.croissant.data.repository.CheckInRepositoryImpl
import com.joeloewi.croissant.data.repository.FailureLogRepositoryImpl
import com.joeloewi.croissant.data.repository.GameRepositoryImpl
import com.joeloewi.croissant.data.repository.HoYoLABRepositoryImpl
import com.joeloewi.croissant.data.repository.ResinStatusWidgetRepositoryImpl
import com.joeloewi.croissant.data.repository.ResultCountRepositoryImpl
import com.joeloewi.croissant.data.repository.ResultRangeRepositoryImpl
import com.joeloewi.croissant.data.repository.SettingsRepositoryImpl
import com.joeloewi.croissant.data.repository.SuccessLogRepositoryImpl
import com.joeloewi.croissant.data.repository.SystemRepositoryImpl
import com.joeloewi.croissant.data.repository.WorkerExecutionLogRepositoryImpl
import com.joeloewi.croissant.domain.repository.AccountRepository
import com.joeloewi.croissant.domain.repository.ArcaLiveAppRepository
import com.joeloewi.croissant.domain.repository.AttendanceRepository
import com.joeloewi.croissant.domain.repository.CheckInRepository
import com.joeloewi.croissant.domain.repository.FailureLogRepository
import com.joeloewi.croissant.domain.repository.GameRepository
import com.joeloewi.croissant.domain.repository.HoYoLABRepository
import com.joeloewi.croissant.domain.repository.ResinStatusWidgetRepository
import com.joeloewi.croissant.domain.repository.ResultCountRepository
import com.joeloewi.croissant.domain.repository.ResultRangeRepository
import com.joeloewi.croissant.domain.repository.SettingsRepository
import com.joeloewi.croissant.domain.repository.SuccessLogRepository
import com.joeloewi.croissant.domain.repository.SystemRepository
import com.joeloewi.croissant.domain.repository.WorkerExecutionLogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun bindAccountRepository(accountRepositoryImpl: AccountRepositoryImpl): AccountRepository

    @Binds
    fun bindAttendanceRepository(attendanceRepositoryImpl: AttendanceRepositoryImpl): AttendanceRepository

    @Binds
    fun bindFailureLogRepository(failureLogRepositoryImpl: FailureLogRepositoryImpl): FailureLogRepository

    @Binds
    fun bindGameRepository(gameRepositoryImpl: GameRepositoryImpl): GameRepository

    @Binds
    fun bindResinStatusRepository(resinStatusWidgetRepositoryImpl: ResinStatusWidgetRepositoryImpl): ResinStatusWidgetRepository

    @Binds
    fun bindSuccessLogRepository(successLogRepositoryImpl: SuccessLogRepositoryImpl): SuccessLogRepository

    @Binds
    fun bindWorkerExecutionLogRepository(workerExecutionLogRepositoryImpl: WorkerExecutionLogRepositoryImpl): WorkerExecutionLogRepository

    @Binds
    fun bindResultCountRepository(resultCountRepositoryImpl: ResultCountRepositoryImpl): ResultCountRepository

    @Binds
    fun bindResultRangeRepository(resultRangeRepositoryImpl: ResultRangeRepositoryImpl): ResultRangeRepository

    @Binds
    fun bindSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    fun bindHoYoLABRepository(hoYoLABRepositoryImpl: HoYoLABRepositoryImpl): HoYoLABRepository

    @Binds
    fun bindCommonCheckInRepository(checkInRepositoryImpl: CheckInRepositoryImpl): CheckInRepository

    @Binds
    fun bindArcaLiveAppRepository(arcaLiveAppRepositoryImpl: ArcaLiveAppRepositoryImpl): ArcaLiveAppRepository

    @Binds
    fun bindSystemRepository(systemRepositoryImpl: SystemRepositoryImpl): SystemRepository
}