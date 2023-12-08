package com.joeloewi.croissant.state

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Immutable
data class ImmutableWrapper<T>(
    val value: T
)

@Stable
data class StableWrapper<T>(
    val value: T
)