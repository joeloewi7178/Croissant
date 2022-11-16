package com.joeloewi.croissant.util

import android.app.Activity
import android.content.Context
import android.os.Build
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.crashlytics.FirebaseCrashlytics

suspend fun requestReview(
    context: Context,
    activity: Activity,
    logMessage: String?
) {
    //i guess google tests app by this device
    //and it does not have play store app, because i got an error on crashlytics :
    //Review Error(-1): The Play Store app is either not installed or not the official version
    if (!listOf("LG-H790", "LG-H791").contains(Build.MODEL.uppercase())) {
        runCatching {
            ReviewManagerFactory.create(context)
        }.mapCatching { reviewManager ->
            with(reviewManager) {
                launchReview(activity, requestReview())
            }
        }.onFailure { cause ->
            FirebaseCrashlytics.getInstance().apply {
                log(Build.MODEL)
                logMessage?.let { log(it) }
                recordException(cause)
            }
        }
    }
}