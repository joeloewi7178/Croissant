package com.joeloewi.domain.wrapper

sealed class ContentOrError<out T> {
    open val content: T? = null
    open val error: Throwable? = null

    data class Content<out T>(override val content: T) : ContentOrError<T>()
    data class Error(override val error: Throwable) : ContentOrError<Nothing>()
}

fun <T> ContentOrError<T>.getOrThrow(): T = when (this) {
    is ContentOrError.Content -> {
        content
    }
    is ContentOrError.Error -> {
        throw error
    }
}
