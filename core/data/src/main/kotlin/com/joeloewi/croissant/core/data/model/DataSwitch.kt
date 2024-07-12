package com.joeloewi.croissant.core.data.model

import androidx.compose.runtime.Immutable

@Immutable
data class DataSwitch(
    val switchId: Int = Int.MIN_VALUE,
    val isPublic: Boolean = false,
    val switchName: String = ""
) {
    companion object {
        const val GENSHIN_IMPACT_DAILY_NOTE_SWITCH_ID = 3
    }
}