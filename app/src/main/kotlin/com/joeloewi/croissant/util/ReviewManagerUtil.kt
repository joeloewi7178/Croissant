package com.joeloewi.croissant.util

import android.app.Activity
import android.os.Build
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import kotlinx.coroutines.CancellationException

suspend fun requestReview(
    activity: Activity,
    logMessage: String?
) {
    if (!isDeviceNexus5X()) {
        runCatching {
            ReviewManagerFactory.create(activity.applicationContext)
        }.mapCatching { reviewManager ->
            with(reviewManager) {
                launchReview(activity, requestReview())
            }
        }.onFailure { cause ->
            if (cause is CancellationException) {
                throw cause
            }

            Firebase.crashlytics.apply {
                log(Build.MODEL)
                logMessage?.let { log(it) }
                recordException(cause)
            }
        }
    }
}