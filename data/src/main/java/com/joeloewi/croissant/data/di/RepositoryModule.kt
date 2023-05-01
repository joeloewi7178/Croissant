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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindAccountRepository(accountRepositoryImpl: AccountRepositoryImpl): AccountRepository

    @Binds
    @Singleton
    fun bindAttendanceRepository(attendanceRepositoryImpl: AttendanceRepositoryImpl): AttendanceRepository

    @Binds
    @Singleton
    fun bindFailureLogRepository(failureLogRepositoryImpl: FailureLogRepositoryImpl): FailureLogRepository

    @Binds
    @Singleton
    fun bindGameRepository(gameRepositoryImpl: GameRepositoryImpl): GameRepository

    @Binds
    @Singleton
    fun bindResinStatusRepository(resinStatusWidgetRepositoryImpl: ResinStatusWidgetRepositoryImpl): ResinStatusWidgetRepository

    @Binds
    @Singleton
    fun bindSuccessLogRepository(successLogRepositoryImpl: SuccessLogRepositoryImpl): SuccessLogRepository

    @Binds
    @Singleton
    fun bindWorkerExecutionLogRepository(workerExecutionLogRepositoryImpl: WorkerExecutionLogRepositoryImpl): WorkerExecutionLogRepository

    @Binds
    @Singleton
    fun bindSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    fun bindHoYoLABRepository(hoYoLABRepositoryImpl: HoYoLABRepositoryImpl): HoYoLABRepository

    @Binds
    @Singleton
    fun bindGenshinImpactCheckInRepository(genshinImpactCheckInRepositoryImpl: GenshinImpactCheckInRepositoryImpl): GenshinImpactCheckInRepository

    @Binds
    @Singleton
    fun bindHonkaiImpact3rdCheckInRepository(honkaiImpact3rdCheckInRepositoryImpl: HonkaiImpact3rdCheckInRepositoryImpl): HonkaiImpact3rdCheckInRepository

    @Binds
    @Singleton
    fun bindCommonCheckInRepository(commonCheckInRepositoryImpl: CommonCheckInRepositoryImpl): CommonCheckInRepository

    @Binds
    @Singleton
    fun bindArcaLiveAppRepository(arcaLiveAppRepositoryImpl: ArcaLiveAppRepositoryImpl): ArcaLiveAppRepository
}