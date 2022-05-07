package com.joeloewi.data.di

import android.app.Application
import androidx.room.Room
import com.joeloewi.data.db.CroissantDatabase
import com.joeloewi.data.db.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @Provides
    @Singleton
    fun provideCroissantDataBase(application: Application): CroissantDatabase =
        Room.databaseBuilder(
            application,
            CroissantDatabase::class.java,
            "croissant"
        ).build()

    @Provides
    @Singleton
    fun provideAccountDao(croissantDatabase: CroissantDatabase): AccountDao =
        croissantDatabase.accountDao()

    @Provides
    @Singleton
    fun provideAttendanceDao(croissantDatabase: CroissantDatabase): AttendanceDao =
        croissantDatabase.attendanceDao()

    @Provides
    @Singleton
    fun provideFailureLogDao(croissantDatabase: CroissantDatabase): FailureLogDao =
        croissantDatabase.failureLogDao()

    @Provides
    @Singleton
    fun provideGameDao(croissantDatabase: CroissantDatabase): GameDao = croissantDatabase.gameDao()

    @Provides
    @Singleton
    fun provideResinStatusWidgetDao(croissantDatabase: CroissantDatabase): ResinStatusWidgetDao =
        croissantDatabase.resinStatusWidgetDao()

    @Provides
    @Singleton
    fun provideSuccessLogDao(croissantDatabase: CroissantDatabase): SuccessLogDao =
        croissantDatabase.successLogDao()

    @Provides
    @Singleton
    fun provideWorkerExecutionLogDao(croissantDatabase: CroissantDatabase): WorkerExecutionLogDao =
        croissantDatabase.workerExecutionLogDao()
}