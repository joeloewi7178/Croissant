package com.joeloewi.croissant.receiver

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.WorkManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.joeloewi.croissant.util.goAsync
import com.joeloewi.domain.usecase.AttendanceUseCase
import com.joeloewi.domain.usecase.ResinStatusWidgetUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import javax.inject.Inject

@AndroidEntryPoint
class MigrationHelper : BroadcastReceiver() {

    @Inject
    lateinit var application: Application

    @Inject
    lateinit var getAllResinStatusWidgetUseCase: ResinStatusWidgetUseCase.GetAll

    @Inject
    lateinit var updateResinStatusWidgetUseCase: ResinStatusWidgetUseCase.Update

    @Inject
    lateinit var getAllOneShotAttendanceUseCase: AttendanceUseCase.GetAllOneShot

    override fun onReceive(p0: Context?, p1: Intent?) {
        FirebaseCrashlytics.getInstance().apply {
            log(this@MigrationHelper.javaClass.simpleName)
        }

        when (p1?.action) {
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                goAsync(
                    onError = { cause ->
                        FirebaseCrashlytics.getInstance().apply {
                            recordException(cause)
                        }
                    },
                    coroutineContext = Dispatchers.IO
                ) {
                    //because work manager's job can be deferred, cancel check in event worker
                    //instead of work manager, use alarm manager
                    getAllOneShotAttendanceUseCase().map { attendance ->
                        async(Dispatchers.IO) {
                            WorkManager.getInstance(application)
                                .cancelUniqueWork(attendance.attendCheckInEventWorkerName.toString())
                        }
                    }.awaitAll()
                }
            }

            else -> {

            }
        }
    }
}