package com.joeloewi.croissant.util

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResinStatus(
    val id: Long = 0,
    val nickname: String = "",
    val currentResin: Int = 0,
    val maxResin: Int = 0
) : Parcelable
