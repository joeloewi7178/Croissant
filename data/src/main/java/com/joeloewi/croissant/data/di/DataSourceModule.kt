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

import com.joeloewi.croissant.data.repository.local.*
import com.joeloewi.croissant.data.repository.local.impl.*
import com.joeloewi.croissant.data.repository.remote.*
import com.joeloewi.croissant.data.repository.remote.impl.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataSourceModule {

    @Binds
    @Singleton
    fun bindAccountDataSource(accountDataSourceImpl: AccountDataSourceImpl): AccountDataSource

    @Binds
    @Singleton
    fun bindAttendanceDataSource(attendanceDataSourceImpl: AttendanceDataSourceImpl): AttendanceDataSource

    @Binds
    @Singleton
    fun bindFailureLogDataSource(failureLogDataSourceImpl: FailureLogDataSourceImpl): FailureLogDataSource

    @Binds
    @Singleton
    fun bindGameDataSource(gameDataSourceImpl: GameDataSourceImpl): GameDataSource

    @Binds
    @Singleton
    fun bindResinStatusWidgetDataSource(resinStatusWidgetDataSourceImpl: ResinStatusWidgetDataSourceImpl): ResinStatusWidgetDataSource

    @Binds
    @Singleton
    fun bindSuccessLogDataSource(successLogDataSourceImpl: SuccessLogDataSourceImpl): SuccessLogDataSource

    @Binds
    @Singleton
    fun bindWorkerExecutionLogDataSource(workerExecutionLogDataSourceImpl: WorkerExecutionLogDataSourceImpl): WorkerExecutionLogDataSource

    @Binds
    @Singleton
    fun bindSettingsDataSource(settingsDateSourceImpl: SettingsDateSourceImpl): SettingsDataSource

    @Binds
    @Singleton
    fun bindHoYoLABDataSource(hoYoLABDataSourceImpl: HoYoLABDataSourceImpl): HoYoLABDataSource

    @Binds
    @Singleton
    fun bindGenshinImpactCheckInDataSource(genshinImpactCheckInDataSourceImpl: GenshinImpactCheckInDataSourceImpl): GenshinImpactCheckInDataSource

    @Binds
    @Singleton
    fun bindHonkaiImpact3rdCheckInDataSource(honkaiImpact3rdCheckInDataSourceImpl: HonkaiImpact3rdCheckInDataSourceImpl): HonkaiImpact3rdCheckInDataSource

    @Binds
    @Singleton
    fun bindTearsOfThemisCheckInDataSource(tearsOfThemisCheckInDataSourceImpl: TearsOfThemisCheckInDataSourceImpl): TearsOfThemisCheckInDataSource

    @Binds
    @Singleton
    fun bindArcaLiveAppDataSource(arcaLiveAppAppDataSourceImpl: ArcaLiveAppDataSourceImpl): ArcaLiveAppDataSource
}