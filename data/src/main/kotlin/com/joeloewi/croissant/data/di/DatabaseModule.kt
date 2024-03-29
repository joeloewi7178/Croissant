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

import android.content.Context
import androidx.room.Room
import com.joeloewi.croissant.data.database.CroissantDatabase
import com.joeloewi.croissant.data.database.dao.AccountDao
import com.joeloewi.croissant.data.database.dao.AttendanceDao
import com.joeloewi.croissant.data.database.dao.FailureLogDao
import com.joeloewi.croissant.data.database.dao.GameDao
import com.joeloewi.croissant.data.database.dao.ResinStatusWidgetDao
import com.joeloewi.croissant.data.database.dao.ResultCountDao
import com.joeloewi.croissant.data.database.dao.ResultRangeDao
import com.joeloewi.croissant.data.database.dao.SuccessLogDao
import com.joeloewi.croissant.data.database.dao.WorkerExecutionLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideCroissantDatabase(
        @IoDispatcherExecutor ioDispatcherExecutor: Executor,
        @ApplicationContext context: Context
    ): CroissantDatabase =
        Room.databaseBuilder(
            context,
            CroissantDatabase::class.java,
            "croissant"
        )
            .setQueryExecutor(ioDispatcherExecutor)
            .setTransactionExecutor(ioDispatcherExecutor)
            .enableMultiInstanceInvalidation()
            .build()

    @Provides
    fun provideAccountDao(croissantDatabase: CroissantDatabase): AccountDao =
        croissantDatabase.accountDao()

    @Provides
    fun provideAttendanceDao(croissantDatabase: CroissantDatabase): AttendanceDao =
        croissantDatabase.attendanceDao()

    @Provides
    fun provideFailureLogDao(croissantDatabase: CroissantDatabase): FailureLogDao =
        croissantDatabase.failureLogDao()

    @Provides
    fun provideGameDao(croissantDatabase: CroissantDatabase): GameDao = croissantDatabase.gameDao()

    @Provides
    fun provideResinStatusWidgetDao(croissantDatabase: CroissantDatabase): ResinStatusWidgetDao =
        croissantDatabase.resinStatusWidgetDao()

    @Provides
    fun provideSuccessLogDao(croissantDatabase: CroissantDatabase): SuccessLogDao =
        croissantDatabase.successLogDao()

    @Provides
    fun provideWorkerExecutionLogDao(croissantDatabase: CroissantDatabase): WorkerExecutionLogDao =
        croissantDatabase.workerExecutionLogDao()

    @Provides
    fun provideResultCountDao(croissantDatabase: CroissantDatabase): ResultCountDao =
        croissantDatabase.resultCountDao()

    @Provides
    fun provideResultRangeDao(croissantDatabase: CroissantDatabase): ResultRangeDao =
        croissantDatabase.resultRangeDao()
}