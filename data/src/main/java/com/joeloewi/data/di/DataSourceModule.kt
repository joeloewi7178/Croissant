package com.joeloewi.data.di

import com.joeloewi.data.repository.local.*
import com.joeloewi.data.repository.local.impl.*
import com.joeloewi.data.repository.remote.GenshinImpactCheckInDataSource
import com.joeloewi.data.repository.remote.HoYoLABDataSource
import com.joeloewi.data.repository.remote.HonkaiImpact3rdCheckInDataSource
import com.joeloewi.data.repository.remote.TearsOfThemisCheckInDataSource
import com.joeloewi.data.repository.remote.impl.GenshinImpactCheckInDataSourceImpl
import com.joeloewi.data.repository.remote.impl.HoYoLABDataSourceImpl
import com.joeloewi.data.repository.remote.impl.HonkaiImpact3rdCheckInDataSourceImpl
import com.joeloewi.data.repository.remote.impl.TearsOfThemisCheckInDataSourceImpl
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
}