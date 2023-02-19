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

import com.joeloewi.croissant.data.mapper.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapperModule {

    @Singleton
    @Provides
    fun provideAccountMapper(): AccountMapper = AccountMapper()

    @Singleton
    @Provides
    fun provideAttendanceMapper(): AttendanceMapper = AttendanceMapper()

    @Singleton
    @Provides
    fun provideGameMapper(): GameMapper = GameMapper()

    @Singleton
    @Provides
    fun provideFailureLogMapper(): FailureLogMapper = FailureLogMapper()

    @Singleton
    @Provides
    fun provideAttendanceWithGamesMapper(
        attendanceMapper: AttendanceMapper,
        gameMapper: GameMapper
    ): AttendanceWithGamesMapper = AttendanceWithGamesMapper(attendanceMapper, gameMapper)

    @Singleton
    @Provides
    fun provideResinStatusWidgetMapper(): ResinStatusWidgetMapper = ResinStatusWidgetMapper()

    @Singleton
    @Provides
    fun provideResinStatusWidgetWithAccountsMapper(
        resinStatusWidgetMapper: ResinStatusWidgetMapper,
        accountMapper: AccountMapper
    ): ResinStatusWithAccountsMapper =
        ResinStatusWithAccountsMapper(resinStatusWidgetMapper, accountMapper)

    @Singleton
    @Provides
    fun provideSettingsMapper(): SettingsMapper = SettingsMapper()

    @Singleton
    @Provides
    fun provideSuccessLogMapper(): SuccessLogMapper = SuccessLogMapper()

    @Singleton
    @Provides
    fun provideWorkerExecutionMapper(): WorkerExecutionLogMapper = WorkerExecutionLogMapper()

    @Singleton
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

    @Singleton
    @Provides
    fun provideUserInfoMapper(): UserInfoMapper = UserInfoMapper()

    @Singleton
    @Provides
    fun provideUserFullInfoDataMapper(
        userInfoMapper: UserInfoMapper
    ): UserFullInfoDataMapper = UserFullInfoDataMapper(userInfoMapper)

    @Singleton
    @Provides
    fun provideUserFullInfoMapper(
        userFullInfoDataMapper: UserFullInfoDataMapper
    ): UserFullInfoMapper = UserFullInfoMapper(userFullInfoDataMapper)

    @Singleton
    @Provides
    fun provideDataSwitchMapper(): DataSwitchMapper = DataSwitchMapper()

    @Singleton
    @Provides
    fun provideGameRecordMapper(
        dataSwitchMapper: DataSwitchMapper
    ): GameRecordMapper = GameRecordMapper(dataSwitchMapper)

    @Singleton
    @Provides
    fun provideGameRecordCardDataMapper(
        gameRecordMapper: GameRecordMapper
    ): GameRecordCardDataMapper = GameRecordCardDataMapper(gameRecordMapper)

    @Singleton
    @Provides
    fun provideGenshinDailyNoteDataMapper(): GenshinDailyNoteDataMapper =
        GenshinDailyNoteDataMapper()
}