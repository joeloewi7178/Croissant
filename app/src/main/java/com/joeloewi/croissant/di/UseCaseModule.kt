package com.joeloewi.croissant.di

import com.joeloewi.domain.repository.*
import com.joeloewi.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideInsertAccountUseCase(accountRepository: AccountRepository): AccountUseCase.Insert =
        AccountUseCase.Insert(accountRepository)

    @Provides
    @Singleton
    fun provideInsertAttendanceUseCase(attendanceRepository: AttendanceRepository): AttendanceUseCase.Insert =
        AttendanceUseCase.Insert(attendanceRepository)

    @Provides
    @Singleton
    fun provideUpdateAttendanceUseCase(attendanceRepository: AttendanceRepository): AttendanceUseCase.Update =
        AttendanceUseCase.Update(attendanceRepository)

    @Provides
    @Singleton
    fun provideDeleteAttendanceUseCase(attendanceRepository: AttendanceRepository): AttendanceUseCase.Delete =
        AttendanceUseCase.Delete(attendanceRepository)

    @Provides
    @Singleton
    fun provideGetOneAttendanceUseCase(attendanceRepository: AttendanceRepository): AttendanceUseCase.GetOne =
        AttendanceUseCase.GetOne(attendanceRepository)

    @Provides
    @Singleton
    fun provideGetByUidAttendanceUseCase(attendanceRepository: AttendanceRepository): AttendanceUseCase.GetOneByUid =
        AttendanceUseCase.GetOneByUid(attendanceRepository)

    @Provides
    @Singleton
    fun provideGetByIdsAttendancesUseCase(attendanceRepository: AttendanceRepository): AttendanceUseCase.GetByIds =
        AttendanceUseCase.GetByIds(attendanceRepository)

    @Provides
    @Singleton
    fun provideGetAllPagedAttendanceUseCase(attendanceRepository: AttendanceRepository): AttendanceUseCase.GetAllPaged =
        AttendanceUseCase.GetAllPaged(attendanceRepository)


    @Provides
    @Singleton
    fun provideInsertFailureLogUseCase(failureLogRepository: FailureLogRepository): FailureLogUseCase.Insert =
        FailureLogUseCase.Insert(failureLogRepository)

    @Provides
    @Singleton
    fun provideInsertGameUseCase(gameRepository: GameRepository): GameUseCase.Insert =
        GameUseCase.Insert(gameRepository)

    @Provides
    @Singleton
    fun provideUpdateGameUseCase(gameRepository: GameRepository): GameUseCase.Update =
        GameUseCase.Update(gameRepository)

    @Provides
    @Singleton
    fun provideDeleteGameUseCase(gameRepository: GameRepository): GameUseCase.Delete =
        GameUseCase.Delete(gameRepository)

    @Provides
    @Singleton
    fun provideGetUserFullInfoHoYoLABUseCase(hoYoLABRepository: HoYoLABRepository): HoYoLABUseCase.GetUserFullInfo =
        HoYoLABUseCase.GetUserFullInfo(hoYoLABRepository)

    @Provides
    @Singleton
    fun provideGetGameRecordCardHoYoLABUseCase(hoYoLABRepository: HoYoLABRepository): HoYoLABUseCase.GetGameRecordCard =
        HoYoLABUseCase.GetGameRecordCard(hoYoLABRepository)

    @Provides
    @Singleton
    fun provideGetGenshinDailyNoteHoYoLABUseCase(hoYoLABRepository: HoYoLABRepository): HoYoLABUseCase.GetGenshinDailyNote =
        HoYoLABUseCase.GetGenshinDailyNote(hoYoLABRepository)

    @Provides
    @Singleton
    fun provideChangeDataSwitchHoYoLABUseCase(hoYoLABRepository: HoYoLABRepository): HoYoLABUseCase.ChangeDataSwitch =
        HoYoLABUseCase.ChangeDataSwitch(hoYoLABRepository)

    @Provides
    @Singleton
    fun provideAttendCheckInGenshinImpactUseCase(genshinImpactCheckInRepository: GenshinImpactCheckInRepository): GenshinImpactCheckInUseCase.AttendCheckInGenshinImpact =
        GenshinImpactCheckInUseCase.AttendCheckInGenshinImpact(genshinImpactCheckInRepository)

    @Provides
    @Singleton
    fun provideAttendCheckInHonkaiImpact3rdUseCase(honkaiImpact3rdCheckInRepository: HonkaiImpact3rdCheckInRepository): HonkaiImpact3rdCheckInUseCase.AttendCheckInHonkaiImpact3rd =
        HonkaiImpact3rdCheckInUseCase.AttendCheckInHonkaiImpact3rd(honkaiImpact3rdCheckInRepository)

    @Provides
    @Singleton
    fun provideAttendCheckInTearsOfThemisUseCase(tearsOfThemisCheckInRepository: TearsOfThemisCheckInRepository): TearsOfThemisCheckInUseCase.AttendCheckInTearsOfThemis =
        TearsOfThemisCheckInUseCase.AttendCheckInTearsOfThemis(tearsOfThemisCheckInRepository)

    @Provides
    @Singleton
    fun provideInsertResinStatusWidgetUseCase(resinStatusWidgetRepository: ResinStatusWidgetRepository): ResinStatusWidgetUseCase.Insert =
        ResinStatusWidgetUseCase.Insert(resinStatusWidgetRepository)

    @Provides
    @Singleton
    fun provideDeleteResinStatusWidgetUseCase(resinStatusWidgetRepository: ResinStatusWidgetRepository): ResinStatusWidgetUseCase.Delete =
        ResinStatusWidgetUseCase.Delete(resinStatusWidgetRepository)

    @Provides
    @Singleton
    fun provideUpdateResinStatusWidgetUseCase(resinStatusWidgetRepository: ResinStatusWidgetRepository): ResinStatusWidgetUseCase.Update =
        ResinStatusWidgetUseCase.Update(resinStatusWidgetRepository)

    @Provides
    @Singleton
    fun provideGetOneResinStatusWidgetUseCase(resinStatusWidgetRepository: ResinStatusWidgetRepository): ResinStatusWidgetUseCase.GetOne =
        ResinStatusWidgetUseCase.GetOne(resinStatusWidgetRepository)

    @Provides
    @Singleton
    fun provideDeleteByAppWidgetIdResinStatusWidgetUseCase(resinStatusWidgetRepository: ResinStatusWidgetRepository): ResinStatusWidgetUseCase.DeleteByAppWidgetId =
        ResinStatusWidgetUseCase.DeleteByAppWidgetId(resinStatusWidgetRepository)

    @Provides
    @Singleton
    fun provideGetOneByAppWidgetIdResinStatusWidgetUseCase(resinStatusWidgetRepository: ResinStatusWidgetRepository): ResinStatusWidgetUseCase.GetOneByAppWidgetId =
        ResinStatusWidgetUseCase.GetOneByAppWidgetId(resinStatusWidgetRepository)

    @Provides
    @Singleton
    fun provideGetSettingsUseCase(settingsRepository: SettingsRepository): SettingsUseCase.GetSettings =
        SettingsUseCase.GetSettings(settingsRepository)

    @Provides
    @Singleton
    fun provideSetDarkThemeEnabledSettingsUseCase(settingsRepository: SettingsRepository): SettingsUseCase.SetDarkThemeEnabled =
        SettingsUseCase.SetDarkThemeEnabled(settingsRepository)

    @Provides
    @Singleton
    fun provideSetIsFirstLaunchSettingsUseCase(settingsRepository: SettingsRepository): SettingsUseCase.SetIsFirstLaunch =
        SettingsUseCase.SetIsFirstLaunch(settingsRepository)

    @Provides
    @Singleton
    fun provideInsertSuccessLogUseCase(successLogRepository: SuccessLogRepository): SuccessLogUseCase.Insert =
        SuccessLogUseCase.Insert(successLogRepository)

    @Provides
    @Singleton
    fun provideInsertWorkerExecutionLogUseCase(workerExecutionLogRepository: WorkerExecutionLogRepository): WorkerExecutionLogUseCase.Insert =
        WorkerExecutionLogUseCase.Insert(workerExecutionLogRepository)

    @Provides
    @Singleton
    fun provideDeleteWorkerExecutionLogUseCase(workerExecutionLogRepository: WorkerExecutionLogRepository): WorkerExecutionLogUseCase.Delete =
        WorkerExecutionLogUseCase.Delete(workerExecutionLogRepository)

    @Provides
    @Singleton
    fun provideDeleteAllWorkerExecutionLogUseCase(workerExecutionLogRepository: WorkerExecutionLogRepository): WorkerExecutionLogUseCase.DeleteAll =
        WorkerExecutionLogUseCase.DeleteAll(workerExecutionLogRepository)

    @Provides
    @Singleton
    fun provideGetAllPagedWorkerExecutionLogUseCase(workerExecutionLogRepository: WorkerExecutionLogRepository): WorkerExecutionLogUseCase.GetAllPaged =
        WorkerExecutionLogUseCase.GetAllPaged(workerExecutionLogRepository)

    @Provides
    @Singleton
    fun provideGetCountByStateWorkerExecutionLogUseCase(workerExecutionLogRepository: WorkerExecutionLogRepository): WorkerExecutionLogUseCase.GetCountByState =
        WorkerExecutionLogUseCase.GetCountByState(workerExecutionLogRepository)

    @Provides
    @Singleton
    fun provideGetAllOneShotAttendanceUseCase(attendanceRepository: AttendanceRepository): AttendanceUseCase.GetAllOneShot =
        AttendanceUseCase.GetAllOneShot(attendanceRepository)
}