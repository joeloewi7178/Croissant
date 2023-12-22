package com.joeloewi.croissant.util

import android.app.PendingIntent
import android.os.Build

val pendingIntentFlagUpdateCurrent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
} else {
    PendingIntent.FLAG_UPDATE_CURRENT
}