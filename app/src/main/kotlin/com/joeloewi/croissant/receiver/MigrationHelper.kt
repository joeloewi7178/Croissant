package com.joeloewi.croissant.receiver

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkManager
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.joeloewi.croissant.domain.usecase.AttendanceUseCase
import com.joeloewi.croissant.domain.usecase.ResinStatusWidgetUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MigrationHelper : BroadcastReceiver() {
    private val _coroutineContext = Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
        Firebase.crashlytics.apply {
            log(this@MigrationHelper.javaClass.simpleName)
            recordException(throwable)
        }
    }
    private val _processLifecycleScope by lazy { ProcessLifecycleOwner.get().lifecycleScope }

    @Inject
    lateinit var application: Application

    @Inject
    lateinit var getAllResinStatusWidgetUseCase: ResinStatusWidgetUseCase.GetAll

    @Inject
    lateinit var updateResinStatusWidgetUseCase: ResinStatusWidgetUseCase.Update

    @Inject
    lateinit var getAllOneShotAttendanceUseCase: AttendanceUseCase.GetAllOneShot

    @Inject
    lateinit var workManager: WorkManager

    override fun onReceive(p0: Context, p1: Intent) {
        when (p1.action) {
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                _processLifecycleScope.launch(_coroutineContext) {
                    //because work manager's job can be deferred, cancel check in event worker
                    //instead of work manager, use alarm manager
                    getAllOneShotAttendanceUseCase().map { attendance ->
                        async(SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, _ -> }) {
                            workManager.cancelUniqueWork(attendance.attendCheckInEventWorkerName.toString())
                        }
                    }.awaitAll()
                }
            }

            else -> {

            }
        }
    }
}