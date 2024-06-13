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

import com.joeloewi.croissant.core.data.repository.AccountRepositoryImpl
import com.joeloewi.croissant.core.data.repository.ArcaLiveAppRepositoryImpl
import com.joeloewi.croissant.core.data.repository.AttendanceRepositoryImpl
import com.joeloewi.croissant.core.data.repository.CheckInRepositoryImpl
import com.joeloewi.croissant.core.data.repository.FailureLogRepositoryImpl
import com.joeloewi.croissant.core.data.repository.GameRepositoryImpl
import com.joeloewi.croissant.core.data.repository.HoYoLABRepositoryImpl
import com.joeloewi.croissant.core.data.repository.ResinStatusWidgetRepositoryImpl
import com.joeloewi.croissant.core.data.repository.ResultCountRepositoryImpl
import com.joeloewi.croissant.core.data.repository.ResultRangeRepositoryImpl
import com.joeloewi.croissant.core.data.repository.SettingsRepositoryImpl
import com.joeloewi.croissant.core.data.repository.SuccessLogRepositoryImpl
import com.joeloewi.croissant.core.data.repository.SystemRepositoryImpl
import com.joeloewi.croissant.core.data.repository.WorkerExecutionLogRepositoryImpl
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
    fun bindAccountRepository(accountRepositoryImpl: com.joeloewi.croissant.core.data.repository.AccountRepositoryImpl): AccountRepository

    @Binds
    fun bindAttendanceRepository(attendanceRepositoryImpl: com.joeloewi.croissant.core.data.repository.AttendanceRepositoryImpl): AttendanceRepository

    @Binds
    fun bindFailureLogRepository(failureLogRepositoryImpl: com.joeloewi.croissant.core.data.repository.FailureLogRepositoryImpl): FailureLogRepository

    @Binds
    fun bindGameRepository(gameRepositoryImpl: com.joeloewi.croissant.core.data.repository.GameRepositoryImpl): GameRepository

    @Binds
    fun bindResinStatusRepository(resinStatusWidgetRepositoryImpl: com.joeloewi.croissant.core.data.repository.ResinStatusWidgetRepositoryImpl): ResinStatusWidgetRepository

    @Binds
    fun bindSuccessLogRepository(successLogRepositoryImpl: com.joeloewi.croissant.core.data.repository.SuccessLogRepositoryImpl): SuccessLogRepository

    @Binds
    fun bindWorkerExecutionLogRepository(workerExecutionLogRepositoryImpl: com.joeloewi.croissant.core.data.repository.WorkerExecutionLogRepositoryImpl): WorkerExecutionLogRepository

    @Binds
    fun bindResultCountRepository(resultCountRepositoryImpl: com.joeloewi.croissant.core.data.repository.ResultCountRepositoryImpl): ResultCountRepository

    @Binds
    fun bindResultRangeRepository(resultRangeRepositoryImpl: com.joeloewi.croissant.core.data.repository.ResultRangeRepositoryImpl): ResultRangeRepository

    @Binds
    fun bindSettingsRepository(settingsRepositoryImpl: com.joeloewi.croissant.core.data.repository.SettingsRepositoryImpl): SettingsRepository

    @Binds
    fun bindHoYoLABRepository(hoYoLABRepositoryImpl: com.joeloewi.croissant.core.data.repository.HoYoLABRepositoryImpl): HoYoLABRepository

    @Binds
    fun bindCommonCheckInRepository(checkInRepositoryImpl: com.joeloewi.croissant.core.data.repository.CheckInRepositoryImpl): CheckInRepository

    @Binds
    fun bindArcaLiveAppRepository(arcaLiveAppRepositoryImpl: com.joeloewi.croissant.core.data.repository.ArcaLiveAppRepositoryImpl): ArcaLiveAppRepository

    @Binds
    fun bindSystemRepository(systemRepositoryImpl: com.joeloewi.croissant.core.data.repository.SystemRepositoryImpl): SystemRepository
}