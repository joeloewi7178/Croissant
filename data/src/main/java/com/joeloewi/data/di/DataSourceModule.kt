package com.joeloewi.data.di

import com.joeloewi.data.repository.local.*
import com.joeloewi.data.repository.local.impl.*
import com.joeloewi.data.repository.remote.HoYoLABDataSource
import com.joeloewi.data.repository.remote.impl.HoYoLABDataSourceImpl
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
}