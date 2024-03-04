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

import com.joeloewi.croissant.data.mapper.AccountMapper
import com.joeloewi.croissant.data.mapper.AttendanceMapper
import com.joeloewi.croissant.data.mapper.AttendanceWithGamesMapper
import com.joeloewi.croissant.data.mapper.DataSwitchMapper
import com.joeloewi.croissant.data.mapper.FailureLogMapper
import com.joeloewi.croissant.data.mapper.GameMapper
import com.joeloewi.croissant.data.mapper.GameRecordCardDataMapper
import com.joeloewi.croissant.data.mapper.GameRecordMapper
import com.joeloewi.croissant.data.mapper.GenshinDailyNoteDataMapper
import com.joeloewi.croissant.data.mapper.ResinStatusWidgetMapper
import com.joeloewi.croissant.data.mapper.ResinStatusWithAccountsMapper
import com.joeloewi.croissant.data.mapper.SettingsMapper
import com.joeloewi.croissant.data.mapper.SuccessLogMapper
import com.joeloewi.croissant.data.mapper.UserFullInfoDataMapper
import com.joeloewi.croissant.data.mapper.UserFullInfoMapper
import com.joeloewi.croissant.data.mapper.UserInfoMapper
import com.joeloewi.croissant.data.mapper.WorkerExecutionLogMapper
import com.joeloewi.croissant.data.mapper.WorkerExecutionLogWithStateMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MapperModule {

    @Provides
    fun provideAccountMapper(): AccountMapper = AccountMapper()

    @Provides
    fun provideAttendanceMapper(): AttendanceMapper = AttendanceMapper()

    @Provides
    fun provideGameMapper(): GameMapper = GameMapper()

    @Provides
    fun provideFailureLogMapper(): FailureLogMapper = FailureLogMapper()

    @Provides
    fun provideAttendanceWithGamesMapper(
        attendanceMapper: AttendanceMapper,
        gameMapper: GameMapper
    ): AttendanceWithGamesMapper = AttendanceWithGamesMapper(attendanceMapper, gameMapper)

    @Provides
    fun provideResinStatusWidgetMapper(): ResinStatusWidgetMapper = ResinStatusWidgetMapper()

    @Provides
    fun provideResinStatusWidgetWithAccountsMapper(
        resinStatusWidgetMapper: ResinStatusWidgetMapper,
        accountMapper: AccountMapper
    ): ResinStatusWithAccountsMapper =
        ResinStatusWithAccountsMapper(resinStatusWidgetMapper, accountMapper)

    @Provides
    fun provideSettingsMapper(): SettingsMapper = SettingsMapper()

    @Provides
    fun provideSuccessLogMapper(): SuccessLogMapper = SuccessLogMapper()

    @Provides
    fun provideWorkerExecutionMapper(): WorkerExecutionLogMapper = WorkerExecutionLogMapper()

    @Provides
    fun provideWorkerExecutionWithState(
        workerExecutionLogMapper: WorkerExecutionLogMapper,
        successLogMapper: SuccessLogMapper,
        failureLogMapper: FailureLogMapper
    ): WorkerExecutionLogWithStateMapper = WorkerExecutionLogWithStateMapper(
        workerExecutionLogMapper,
        successLogMapper,
        failureLogMapper
    )

    @Provides
    fun provideUserInfoMapper(): UserInfoMapper = UserInfoMapper()

    @Provides
    fun provideUserFullInfoDataMapper(
        userInfoMapper: UserInfoMapper
    ): UserFullInfoDataMapper = UserFullInfoDataMapper(userInfoMapper)

    @Provides
    fun provideUserFullInfoMapper(
        userFullInfoDataMapper: UserFullInfoDataMapper
    ): UserFullInfoMapper = UserFullInfoMapper(userFullInfoDataMapper)

    @Provides
    fun provideDataSwitchMapper(): DataSwitchMapper = DataSwitchMapper()

    @Provides
    fun provideGameRecordMapper(
        dataSwitchMapper: DataSwitchMapper
    ): GameRecordMapper = GameRecordMapper(dataSwitchMapper)

    @Provides
    fun provideGameRecordCardDataMapper(
        gameRecordMapper: GameRecordMapper
    ): GameRecordCardDataMapper = GameRecordCardDataMapper(gameRecordMapper)

    @Provides
    fun provideGenshinDailyNoteDataMapper(): GenshinDailyNoteDataMapper =
        GenshinDailyNoteDataMapper()
}