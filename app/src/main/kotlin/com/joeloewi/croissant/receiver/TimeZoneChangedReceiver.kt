package com.joeloewi.croissant.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.joeloewi.croissant.util.NotificationGenerator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class TimeZoneChangedReceiver @Inject constructor(
) : BroadcastReceiver() {
    private val _coroutineContext = Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
        Firebase.crashlytics.apply {
            log(this@TimeZoneChangedReceiver.javaClass.simpleName)
            recordException(throwable)
        }
    }
    private val _processLifecycleScope by lazy { ProcessLifecycleOwner.get().lifecycleScope }

    @Inject
    lateinit var notificationGenerator: NotificationGenerator

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_TIMEZONE_CHANGED -> {
                _processLifecycleScope.launch(_coroutineContext) {
                    with(notificationGenerator) {
                        safeNotify(
                            UUID.randomUUID().toString(),
                            0,
                            createTimezoneChangedNotification()
                        )
                    }
                }
            }

            else -> {

            }
        }
    }
}