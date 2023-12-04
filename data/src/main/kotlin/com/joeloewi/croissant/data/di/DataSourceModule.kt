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

@Module
@InstallIn(SingletonComponent::class)
interface DataSourceModule {

    @Binds
    fun bindAccountDataSource(accountDataSourceImpl: AccountDataSourceImpl): AccountDataSource

    @Binds
    fun bindAttendanceDataSource(attendanceDataSourceImpl: AttendanceDataSourceImpl): AttendanceDataSource

    @Binds
    fun bindFailureLogDataSource(failureLogDataSourceImpl: FailureLogDataSourceImpl): FailureLogDataSource

    @Binds
    fun bindGameDataSource(gameDataSourceImpl: GameDataSourceImpl): GameDataSource

    @Binds
    fun bindResinStatusWidgetDataSource(resinStatusWidgetDataSourceImpl: ResinStatusWidgetDataSourceImpl): ResinStatusWidgetDataSource

    @Binds
    fun bindSuccessLogDataSource(successLogDataSourceImpl: SuccessLogDataSourceImpl): SuccessLogDataSource

    @Binds
    fun bindWorkerExecutionLogDataSource(workerExecutionLogDataSourceImpl: WorkerExecutionLogDataSourceImpl): WorkerExecutionLogDataSource

    @Binds
    fun bindSettingsDataSource(settingsDataSourceImpl: SettingsDataSourceImpl): SettingsDataSource

    @Binds
    fun bindHoYoLABDataSource(hoYoLABDataSourceImpl: HoYoLABDataSourceImpl): HoYoLABDataSource

    @Binds
    fun bindGenshinImpactCheckInDataSource(genshinImpactCheckInDataSourceImpl: GenshinImpactCheckInDataSourceImpl): GenshinImpactCheckInDataSource

    @Binds
    fun bindHonkaiImpact3rdCheckInDataSource(honkaiImpact3rdCheckInDataSourceImpl: HonkaiImpact3rdCheckInDataSourceImpl): HonkaiImpact3rdCheckInDataSource

    @Binds
    fun bindCommonCheckInDataSource(commonCheckInDataSourceImpl: CommonCheckInDataSourceImpl): CommonCheckInDataSource

    @Binds
    fun bindArcaLiveAppDataSource(arcaLiveAppAppDataSourceImpl: ArcaLiveAppDataSourceImpl): ArcaLiveAppDataSource
}