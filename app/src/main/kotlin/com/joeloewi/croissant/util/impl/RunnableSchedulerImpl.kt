/*
 * Copyright (c) 2023. Mobidays
 * DO NOT LEAK OUTSIDE
 */

package com.joeloewi.croissant.util.impl

import android.os.Handler
import androidx.work.RunnableScheduler
import javax.inject.Inject

class RunnableSchedulerImpl @Inject constructor(
    private val applicationHandler: Handler
) : RunnableScheduler {
    override fun scheduleWithDelay(delayInMillis: Long, runnable: Runnable) {
        applicationHandler.postDelayed(runnable, delayInMillis)
    }

    override fun cancel(runnable: Runnable) {
        applicationHandler.removeCallbacks(runnable)
    }
}