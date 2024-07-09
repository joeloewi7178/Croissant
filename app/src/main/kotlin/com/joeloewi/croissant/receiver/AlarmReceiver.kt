package com.joeloewi.croissant.receiver

import android.app.AlarmManager
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.joeloewi.croissant.BuildConfig
import com.joeloewi.croissant.domain.usecase.AttendanceUseCase
import com.joeloewi.croissant.util.AlarmScheduler
import com.joeloewi.croissant.worker.AttendCheckInEventWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    private val _coroutineContext = Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
        Firebase.crashlytics.apply {
            log(this@AlarmReceiver.javaClass.simpleName)
            recordException(throwable)
        }
    }

    @Inject
    lateinit var application: Application

    @Inject
    lateinit var getOneAttendanceUseCase: AttendanceUseCase.GetOne

    @Inject
    lateinit var getAllOneShotAttendanceUseCase: AttendanceUseCase.GetAllOneShot

    @Inject
    lateinit var alarmManager: AlarmManager

    @Inject
    lateinit var workManager: WorkManager

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @Inject
    lateinit var coroutineScope: CoroutineScope

    override fun onReceive(p0: Context, p1: Intent) {
        Log.d("123123", p1.action.toString())

        when (p1.action) {
            Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_MY_PACKAGE_REPLACED, AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED -> {
                coroutineScope.launch(_coroutineContext) {
                    getAllOneShotAttendanceUseCase().forEach { attendance ->
                        attendance.runCatching {
                            alarmScheduler.scheduleCheckInAlarm(
                                attendance = attendance,
                                scheduleForTomorrow = false
                            )
                        }
                    }
                }
            }

            RECEIVE_ATTEND_CHECK_IN_ALARM -> {
                coroutineScope.launch(_coroutineContext) {
                    val attendanceId = p1.getLongExtra(ATTENDANCE_ID, Long.MIN_VALUE)
                    val attendanceWithGames = getOneAttendanceUseCase(attendanceId)
                    val attendance = attendanceWithGames.attendance
                    val oneTimeWork =
                        AttendCheckInEventWorker.buildOneTimeWork(attendanceId = attendance.id)

                    workManager.beginUniqueWork(
                        attendance.oneTimeAttendCheckInEventWorkerName.toString(),
                        ExistingWorkPolicy.APPEND_OR_REPLACE,
                        oneTimeWork
                    ).enqueue()

                    alarmScheduler.scheduleCheckInAlarm(
                        attendance = attendance,
                        scheduleForTomorrow = true
                    )
                }
            }

            else -> {

            }
        }
    }

    companion object {
        const val RECEIVE_ATTEND_CHECK_IN_ALARM =
            "${BuildConfig.APPLICATION_ID}.action.RECEIVE_ATTEND_CHECK_IN_ALARM"
        const val ATTENDANCE_ID = "attendanceId"
    }
}