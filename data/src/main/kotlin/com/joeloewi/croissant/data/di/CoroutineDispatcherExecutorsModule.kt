/*
 * Copyright (c) 2023. Mobidays
 * DO NOT LEAK OUTSIDE
 */

package com.joeloewi.croissant.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asExecutor
import java.util.concurrent.Executor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoroutineDispatcherExecutorsModule {

    @Singleton
    @DefaultDispatcherExecutor
    @Provides
    fun providesDefaultDispatcherExecutor(
        @DefaultDispatcher coroutineDispatcher: CoroutineDispatcher
    ): Executor = coroutineDispatcher.asExecutor()

    @Singleton
    @IoDispatcherExecutor
    @Provides
    fun providesIoDispatcherExecutor(
        @IoDispatcher coroutineDispatcher: CoroutineDispatcher
    ): Executor = coroutineDispatcher.asExecutor()

    @Singleton
    @MainDispatcherExecutor
    @Provides
    fun providesMainDispatcherExecutor(
        @MainDispatcher coroutineDispatcher: CoroutineDispatcher
    ): Executor = coroutineDispatcher.asExecutor()

    @Singleton
    @MainImmediateDispatcherExecutor
    @Provides
    fun providesMainImmediateDispatcherExecutor(
        @MainImmediateDispatcher coroutineDispatcher: CoroutineDispatcher
    ): Executor = coroutineDispatcher.asExecutor()
}