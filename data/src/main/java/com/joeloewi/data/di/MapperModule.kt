package com.joeloewi.data.di

import com.joeloewi.data.mapper.*
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