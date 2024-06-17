package com.joeloewi.croissant.core.database.di

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
import com.joeloewi.croissant.core.database.SuccessLogDataSource
import com.joeloewi.croissant.core.database.SuccessLogDataSourceImpl
import com.joeloewi.croissant.core.database.WorkerExecutionLogDataSource
import com.joeloewi.croissant.core.database.WorkerExecutionLogDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseDataSourceModule {

    @Binds
    abstract fun bindAccountDataSource(accountDataSourceImpl: AccountDataSourceImpl): AccountDataSource

    @Binds
    abstract fun bindAttendanceDataSource(attendanceDataSourceImpl: AttendanceDataSourceImpl): AttendanceDataSource

    @Binds
    abstract fun bindFailureLogDataSource(failureLogDataSourceImpl: FailureLogDataSourceImpl): FailureLogDataSource

    @Binds
    abstract fun bindGameDataSource(gameDataSourceImpl: GameDataSourceImpl): GameDataSource

    @Binds
    abstract fun bindResinStatusWidgetDataSource(resinStatusWidgetDataSourceImpl: ResinStatusWidgetDataSourceImpl): ResinStatusWidgetDataSource

    @Binds
    abstract fun bindResultCountDataSource(resultCountDataSourceImpl: ResultCountDataSourceImpl): ResultCountDataSource

    @Binds
    abstract fun bindResultRangeDataSource(resultRangeDataSourceImpl: ResultRangeDataSourceImpl): ResultRangeDataSource

    @Binds
    abstract fun bindSuccessLogDataSource(successLogDataSourceImpl: SuccessLogDataSourceImpl): SuccessLogDataSource

    @Binds
    abstract fun bindWorkerExecutionLogDataSource(workerExecutionLogDataSourceImpl: WorkerExecutionLogDataSourceImpl): WorkerExecutionLogDataSource
}