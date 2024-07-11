package com.joeloewi.croissant.state

import androidx.compose.runtime.Immutable

//inspired by https://github.com/Laimiux/lce

@Immutable
sealed class LCE<out T> {
    open val content: T? = null
    open val error: Throwable? = null
    open val isLoading: Boolean = false

    data object Loading : LCE<Nothing>() {
        override val isLoading: Boolean = true
    }

    data class Content<out T>(override val content: T) : LCE<T>()
    data class Error(override val error: Throwable) : LCE<Nothing>()
}

@Immutable
sealed class ILCE<out T> {
    open val content: T? = null
    open val error: Throwable? = null
    open val isLoading: Boolean = false

    data object Idle : ILCE<Nothing>()
    data object Loading : ILCE<Nothing>() {
        override val isLoading: Boolean = true
    }

    data class Content<out T>(override val content: T) : ILCE<T>()
    data class Error(override val error: Throwable) : ILCE<Nothing>()
}

fun <T> Result<T>.foldAsLce() = fold(
    onSuccess = {
        LCE.Content(it)
    },
    onFailure = {
        LCE.Error(it)
    }
)

fun <T> Result<T>.foldAsILCE() = fold(
    onSuccess = {
        ILCE.Content(it)
    },
    onFailure = {
        ILCE.Error(it)
    }
)
