package com.joeloewi.croissant.data.di

import javax.inject.Qualifier

/**
 * Dispatchers
 */
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DefaultDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainImmediateDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class ApplicationHandlerDispatcher

/**
 * Executors
 */

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DefaultDispatcherExecutor

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcherExecutor

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainDispatcherExecutor

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainImmediateDispatcherExecutor