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

import com.joeloewi.croissant.core.database.AccountDataSource
import com.joeloewi.croissant.core.database.AccountDataSourceImpl
import com.joeloewi.croissant.core.database.AttendanceDataSource
import com.joeloewi.croissant.core.database.AttendanceDataSourceImpl
import com.joeloewi.croissant.core.database.FailureLogDataSource
import com.joeloewi.croissant.core.database.FailureLogDataSourceImpl
import com.joeloewi.croissant.core.database.GameDataSource
import com.joeloewi.croissant.core.database.GameDataSourceImpl
import com.joeloewi.croissant.core.database.ResinStatusWidgetDataSource
import com.joeloewi.croissant.core.database.ResinStatusWidgetDataSourceImpl
import com.joeloewi.croissant.core.database.ResultCountDataSource
import com.joeloewi.croissant.core.database.ResultCountDataSourceImpl
import com.joeloewi.croissant.core.database.ResultRangeDataSource
import com.joeloewi.croissant.core.database.ResultRangeDataSourceImpl
import com.joeloewi.croissant.core.database.SettingsDataSource
import com.joeloewi.croissant.core.database.SettingsDataSourceImpl
import com.joeloewi.croissant.core.database.SuccessLogDataSource
import com.joeloewi.croissant.core.database.SuccessLogDataSourceImpl
import com.joeloewi.croissant.core.database.WorkerExecutionLogDataSource
import com.joeloewi.croissant.core.database.WorkerExecutionLogDataSourceImpl
import com.joeloewi.croissant.core.network.ArcaLiveAppDataSource
import com.joeloewi.croissant.core.network.CheckInDataSource
import com.joeloewi.croissant.core.network.HoYoLABDataSource
import com.joeloewi.croissant.core.network.impl.ArcaLiveAppDataSourceImpl
import com.joeloewi.croissant.core.network.impl.CheckInDataSourceImpl
import com.joeloewi.croissant.core.network.impl.HoYoLABDataSourceImpl
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
    fun bindAccountDataSource(accountDataSourceImpl: com.joeloewi.croissant.core.database.AccountDataSourceImpl): com.joeloewi.croissant.core.database.AccountDataSource

    @Binds
    fun bindAttendanceDataSource(attendanceDataSourceImpl: com.joeloewi.croissant.core.database.AttendanceDataSourceImpl): com.joeloewi.croissant.core.database.AttendanceDataSource

    @Binds
    fun bindFailureLogDataSource(failureLogDataSourceImpl: com.joeloewi.croissant.core.database.FailureLogDataSourceImpl): com.joeloewi.croissant.core.database.FailureLogDataSource

    @Binds
    fun bindGameDataSource(gameDataSourceImpl: com.joeloewi.croissant.core.database.GameDataSourceImpl): com.joeloewi.croissant.core.database.GameDataSource

    @Binds
    fun bindResinStatusWidgetDataSource(resinStatusWidgetDataSourceImpl: com.joeloewi.croissant.core.database.ResinStatusWidgetDataSourceImpl): com.joeloewi.croissant.core.database.ResinStatusWidgetDataSource

    @Binds
    fun bindSuccessLogDataSource(successLogDataSourceImpl: com.joeloewi.croissant.core.database.SuccessLogDataSourceImpl): com.joeloewi.croissant.core.database.SuccessLogDataSource

    @Binds
    fun bindWorkerExecutionLogDataSource(workerExecutionLogDataSourceImpl: com.joeloewi.croissant.core.database.WorkerExecutionLogDataSourceImpl): com.joeloewi.croissant.core.database.WorkerExecutionLogDataSource

    @Binds
    fun bindResultCountDataSource(resultCountDataSourceImpl: com.joeloewi.croissant.core.database.ResultCountDataSourceImpl): com.joeloewi.croissant.core.database.ResultCountDataSource

    @Binds
    fun bindResultRangeDataSource(resultRangeDataSourceImpl: com.joeloewi.croissant.core.database.ResultRangeDataSourceImpl): com.joeloewi.croissant.core.database.ResultRangeDataSource

    @Binds
    fun bindSettingsDataSource(settingsDataSourceImpl: com.joeloewi.croissant.core.database.SettingsDataSourceImpl): com.joeloewi.croissant.core.database.SettingsDataSource

    @Binds
    fun bindHoYoLABDataSource(hoYoLABDataSourceImpl: com.joeloewi.croissant.core.network.impl.HoYoLABDataSourceImpl): com.joeloewi.croissant.core.network.HoYoLABDataSource

    @Binds
    fun bindCommonCheckInDataSource(commonCheckInDataSourceImpl: com.joeloewi.croissant.core.network.impl.CheckInDataSourceImpl): com.joeloewi.croissant.core.network.CheckInDataSource

    @Binds
    fun bindArcaLiveAppDataSource(arcaLiveAppAppDataSourceImpl: com.joeloewi.croissant.core.network.impl.ArcaLiveAppDataSourceImpl): com.joeloewi.croissant.core.network.ArcaLiveAppDataSource

    @Binds
    fun bindSystemDataSource(systemDataSourceImpl: SystemDataSourceImpl): SystemDataSource
}