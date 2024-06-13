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
import com.joeloewi.croissant.core.database.CroissantDatabase
import com.joeloewi.croissant.core.database.dao.AccountDao
import com.joeloewi.croissant.core.database.dao.AttendanceDao
import com.joeloewi.croissant.core.database.dao.FailureLogDao
import com.joeloewi.croissant.core.database.dao.GameDao
import com.joeloewi.croissant.core.database.dao.ResinStatusWidgetDao
import com.joeloewi.croissant.core.database.dao.ResultCountDao
import com.joeloewi.croissant.core.database.dao.ResultRangeDao
import com.joeloewi.croissant.core.database.dao.SuccessLogDao
import com.joeloewi.croissant.core.database.dao.WorkerExecutionLogDao
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
    ): com.joeloewi.croissant.core.database.CroissantDatabase =
        Room.databaseBuilder(
            context,
            com.joeloewi.croissant.core.database.CroissantDatabase::class.java,
            "croissant"
        )
            .setQueryExecutor(ioDispatcherExecutor)
            .setTransactionExecutor(ioDispatcherExecutor)
            .enableMultiInstanceInvalidation()
            .build()

    @Provides
    fun provideAccountDao(croissantDatabase: com.joeloewi.croissant.core.database.CroissantDatabase): com.joeloewi.croissant.core.database.dao.AccountDao =
        croissantDatabase.accountDao()

    @Provides
    fun provideAttendanceDao(croissantDatabase: com.joeloewi.croissant.core.database.CroissantDatabase): com.joeloewi.croissant.core.database.dao.AttendanceDao =
        croissantDatabase.attendanceDao()

    @Provides
    fun provideFailureLogDao(croissantDatabase: com.joeloewi.croissant.core.database.CroissantDatabase): com.joeloewi.croissant.core.database.dao.FailureLogDao =
        croissantDatabase.failureLogDao()

    @Provides
    fun provideGameDao(croissantDatabase: com.joeloewi.croissant.core.database.CroissantDatabase): com.joeloewi.croissant.core.database.dao.GameDao =
        croissantDatabase.gameDao()

    @Provides
    fun provideResinStatusWidgetDao(croissantDatabase: com.joeloewi.croissant.core.database.CroissantDatabase): com.joeloewi.croissant.core.database.dao.ResinStatusWidgetDao =
        croissantDatabase.resinStatusWidgetDao()

    @Provides
    fun provideSuccessLogDao(croissantDatabase: com.joeloewi.croissant.core.database.CroissantDatabase): com.joeloewi.croissant.core.database.dao.SuccessLogDao =
        croissantDatabase.successLogDao()

    @Provides
    fun provideWorkerExecutionLogDao(croissantDatabase: com.joeloewi.croissant.core.database.CroissantDatabase): com.joeloewi.croissant.core.database.dao.WorkerExecutionLogDao =
        croissantDatabase.workerExecutionLogDao()

    @Provides
    fun provideResultCountDao(croissantDatabase: com.joeloewi.croissant.core.database.CroissantDatabase): com.joeloewi.croissant.core.database.dao.ResultCountDao =
        croissantDatabase.resultCountDao()

    @Provides
    fun provideResultRangeDao(croissantDatabase: com.joeloewi.croissant.core.database.CroissantDatabase): com.joeloewi.croissant.core.database.dao.ResultRangeDao =
        croissantDatabase.resultRangeDao()
}