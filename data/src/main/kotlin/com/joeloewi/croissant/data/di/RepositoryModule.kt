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

import com.joeloewi.croissant.data.repository.*
import com.joeloewi.croissant.domain.repository.*
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
    fun bindSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    fun bindHoYoLABRepository(hoYoLABRepositoryImpl: HoYoLABRepositoryImpl): HoYoLABRepository

    @Binds
    fun bindGenshinImpactCheckInRepository(genshinImpactCheckInRepositoryImpl: GenshinImpactCheckInRepositoryImpl): GenshinImpactCheckInRepository

    @Binds
    fun bindHonkaiImpact3rdCheckInRepository(honkaiImpact3rdCheckInRepositoryImpl: HonkaiImpact3rdCheckInRepositoryImpl): HonkaiImpact3rdCheckInRepository

    @Binds
    fun bindCommonCheckInRepository(commonCheckInRepositoryImpl: CommonCheckInRepositoryImpl): CommonCheckInRepository

    @Binds
    fun bindArcaLiveAppRepository(arcaLiveAppRepositoryImpl: ArcaLiveAppRepositoryImpl): ArcaLiveAppRepository
}