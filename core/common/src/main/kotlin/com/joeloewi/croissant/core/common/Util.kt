package com.joeloewi.croissant.core.common

import kotlin.coroutines.cancellation.CancellationException

inline fun <reified T : Throwable, R> Result<R>.except(): Result<R> =
    onFailure { if (it is T) throw it }

fun <R> Result<R>.exceptCancellationException(): Result<R> = except<CancellationException, _>()