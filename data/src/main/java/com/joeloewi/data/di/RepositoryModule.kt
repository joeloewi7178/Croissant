package com.joeloewi.data.di

import com.joeloewi.data.repository.*
import com.joeloewi.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindAccountRepository(accountRepositoryImpl: AccountRepositoryImpl): AccountRepository

    @Binds
    @Singleton
    fun bindAttendanceRepository(attendanceRepositoryImpl: AttendanceRepositoryImpl): AttendanceRepository

    @Binds
    @Singleton
    fun bindFailureLogRepository(failureLogRepositoryImpl: FailureLogRepositoryImpl): FailureLogRepository

    @Binds
    @Singleton
    fun bindGameRepository(gameRepositoryImpl: GameRepositoryImpl): GameRepository

    @Binds
    @Singleton
    fun bindResinStatusRepository(resinStatusWidgetRepositoryImpl: ResinStatusWidgetRepositoryImpl): ResinStatusWidgetRepository

    @Binds
    @Singleton
    fun bindSuccessLogRepository(successLogRepositoryImpl: SuccessLogRepositoryImpl): SuccessLogRepository

    @Binds
    @Singleton
    fun bindWorkerExecutionLogRepository(workerExecutionLogRepositoryImpl: WorkerExecutionLogRepositoryImpl): WorkerExecutionLogRepository

    @Binds
    @Singleton
    fun bindSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    fun bindHoYoLABRepository(hoYoLABRepositoryImpl: HoYoLABRepositoryImpl): HoYoLABRepository

    @Binds
    @Singleton
    fun bindGenshinImpactCheckInRepository(genshinImpactCheckInRepositoryImpl: GenshinImpactCheckInRepositoryImpl): GenshinImpactCheckInRepository

    @Binds
    @Singleton
    fun bindHonkaiImpact3rdCheckInRepository(honkaiImpact3rdCheckInRepositoryImpl: HonkaiImpact3rdCheckInRepositoryImpl): HonkaiImpact3rdCheckInRepository

    @Binds
    @Singleton
    fun bindTearsOfThemisCheckInRepository(themisCheckInRepositoryImpl: TearsOfThemisCheckInRepositoryImpl): TearsOfThemisCheckInRepository

    @Binds
    @Singleton
    fun bindArcaLiveAppRepository(arcaLiveAppRepositoryImpl: ArcaLiveAppRepositoryImpl): ArcaLiveAppRepository
}