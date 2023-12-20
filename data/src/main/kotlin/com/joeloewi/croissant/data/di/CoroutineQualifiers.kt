/*
 * Copyright (c) 2023. Mobidays
 * DO NOT LEAK OUTSIDE
 */

package com.joeloewi.croissant.data.di

import javax.inject.Qualifier

/**
 * Dispatchers
 */
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class DefaultDispatcher

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class IoDispatcher

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class MainDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainImmediateDispatcher

/**
 * Executors
 */

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class DefaultDispatcherExecutor

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class IoDispatcherExecutor

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class MainDispatcherExecutor

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainImmediateDispatcherExecutor