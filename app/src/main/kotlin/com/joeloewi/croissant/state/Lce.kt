package com.joeloewi.croissant.state

import androidx.compose.runtime.Stable

@Stable
sealed class Lce<out T> {
    open val content: T? = null
    open val error: Throwable? = null
    open val isLoading: Boolean = false

    object Loading : Lce<Nothing>() {
        override val isLoading: Boolean = true
    }

    data class Content<out T>(override val content: T) : Lce<T>()
    data class Error(override val error: Throwable) : Lce<Nothing>()
}
