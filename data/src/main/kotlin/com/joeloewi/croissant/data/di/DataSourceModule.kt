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

import com.joeloewi.croissant.data.repository.local.AccountDataSource
import com.joeloewi.croissant.data.repository.local.AttendanceDataSource
import com.joeloewi.croissant.data.repository.local.FailureLogDataSource
import com.joeloewi.croissant.data.repository.local.GameDataSource
import com.joeloewi.croissant.data.repository.local.ResinStatusWidgetDataSource
import com.joeloewi.croissant.data.repository.local.ResultCountDataSource
import com.joeloewi.croissant.data.repository.local.ResultRangeDataSource
import com.joeloewi.croissant.data.repository.local.SettingsDataSource
import com.joeloewi.croissant.data.repository.local.SuccessLogDataSource
import com.joeloewi.croissant.data.repository.local.WorkerExecutionLogDataSource
import com.joeloewi.croissant.data.repository.local.impl.AccountDataSourceImpl
import com.joeloewi.croissant.data.repository.local.impl.AttendanceDataSourceImpl
import com.joeloewi.croissant.data.repository.local.impl.FailureLogDataSourceImpl
import com.joeloewi.croissant.data.repository.local.impl.GameDataSourceImpl
import com.joeloewi.croissant.data.repository.local.impl.ResinStatusWidgetDataSourceImpl
import com.joeloewi.croissant.data.repository.local.impl.ResultCountDataSourceImpl
import com.joeloewi.croissant.data.repository.local.impl.ResultRangeDataSourceImpl
import com.joeloewi.croissant.data.repository.local.impl.SettingsDataSourceImpl
import com.joeloewi.croissant.data.repository.local.impl.SuccessLogDataSourceImpl
import com.joeloewi.croissant.data.repository.local.impl.WorkerExecutionLogDataSourceImpl
import com.joeloewi.croissant.data.repository.remote.ArcaLiveAppDataSource
import com.joeloewi.croissant.data.repository.remote.CheckInDataSource
import com.joeloewi.croissant.data.repository.remote.HoYoLABDataSource
import com.joeloewi.croissant.data.repository.remote.impl.ArcaLiveAppDataSourceImpl
import com.joeloewi.croissant.data.repository.remote.impl.CheckInDataSourceImpl
import com.joeloewi.croissant.data.repository.remote.impl.HoYoLABDataSourceImpl
import com.joeloewi.croissant.data.repository.system.SystemDataSource
import com.joeloewi.croissant.data.repository.system.impl.SystemDataSourceImpl
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
    fun bindResultCountDataSource(resultCountDataSourceImpl: ResultCountDataSourceImpl): ResultCountDataSource

    @Binds
    fun bindResultRangeDataSource(resultRangeDataSourceImpl: ResultRangeDataSourceImpl): ResultRangeDataSource

    @Binds
    fun bindSettingsDataSource(settingsDataSourceImpl: SettingsDataSourceImpl): SettingsDataSource

    @Binds
    fun bindHoYoLABDataSource(hoYoLABDataSourceImpl: HoYoLABDataSourceImpl): HoYoLABDataSource

    @Binds
    fun bindCommonCheckInDataSource(commonCheckInDataSourceImpl: CheckInDataSourceImpl): CheckInDataSource

    @Binds
    fun bindArcaLiveAppDataSource(arcaLiveAppAppDataSourceImpl: ArcaLiveAppDataSourceImpl): ArcaLiveAppDataSource

    @Binds
    fun bindSystemDataSource(systemDataSourceImpl: SystemDataSourceImpl): SystemDataSource
}