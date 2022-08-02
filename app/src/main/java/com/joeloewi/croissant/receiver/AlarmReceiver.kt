package com.joeloewi.croissant.receiver

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import androidx.work.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.joeloewi.croissant.BuildConfig
import com.joeloewi.croissant.util.goAsync
import com.joeloewi.croissant.util.pendingIntentFlagUpdateCurrent
import com.joeloewi.croissant.worker.AttendCheckInEventWorker
import com.joeloewi.domain.usecase.AttendanceUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var application: Application

    @Inject
    lateinit var getOneAttendanceUseCase: AttendanceUseCase.GetOne

    @Inject
    lateinit var getAllOneShotAttendanceUseCase: AttendanceUseCase.GetAllOneShot

    @Inject
    lateinit var alarmManager: AlarmManager

    override fun onReceive(p0: Context?, p1: Intent?) {
        FirebaseCrashlytics.getInstance().apply {
            log(this@AlarmReceiver.javaClass.simpleName)
        }

        when (p1?.action) {
            Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_MY_PACKAGE_REPLACED, AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED -> {
                goAsync(
                    onError = { cause ->
                        FirebaseCrashlytics.getInstance().apply {
                            recordException(cause)
                        }
                    },
                    coroutineContext = Dispatchers.IO
                ) {
                    getAllOneShotAttendanceUseCase().map { attendance ->
                        async(Dispatchers.IO) {
                            attendance.runCatching {
                                val alarmIntent =
                                    Intent(application, AlarmReceiver::class.java).apply {
                                        action = RECEIVE_ATTEND_CHECK_IN_ALARM
                                        putExtra(ATTENDANCE_ID, id)
                                    }

                                val now = Calendar.getInstance()
                                val canExecuteToday =
                                    (now[Calendar.HOUR_OF_DAY] < hourOfDay) || (now[Calendar.HOUR_OF_DAY] == hourOfDay && now[Calendar.MINUTE] < minute)

                                val targetTime = Calendar.getInstance().apply {
                                    time = now.time

                                    if (!canExecuteToday) {
                                        add(Calendar.DATE, 1)
                                    }

                                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                                    set(Calendar.MINUTE, minute)
                                    set(Calendar.SECOND, 30)
                                }

                                val pendingIntent = PendingIntent.getBroadcast(
                                    application,
                                    id.toInt(),
                                    alarmIntent,
                                    pendingIntentFlagUpdateCurrent
                                )

                                with(alarmManager) {
                                    cancel(pendingIntent)
                                    AlarmManagerCompat.setExactAndAllowWhileIdle(
                                        this,
                                        AlarmManager.RTC_WAKEUP,
                                        targetTime.timeInMillis,
                                        pendingIntent
                                    )
                                }
                            }
                        }
                    }.awaitAll()
                }
            }

            RECEIVE_ATTEND_CHECK_IN_ALARM -> {
                val attendanceId = p1.getLongExtra(ATTENDANCE_ID, Long.MIN_VALUE)

                goAsync(
                    onError = { cause ->
                        FirebaseCrashlytics.getInstance().apply {
                            recordException(cause)
                        }
                    },
                    coroutineContext = Dispatchers.IO
                ) {
                    val attendanceWithGames = getOneAttendanceUseCase(attendanceId)
                    val attendance = attendanceWithGames.attendance
                    val oneTimeWork = OneTimeWorkRequestBuilder<AttendCheckInEventWorker>()
                        .setInputData(workDataOf(AttendCheckInEventWorker.ATTENDANCE_ID to attendance.id))
                        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                        .setConstraints(
                            Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build()
                        )
                        .build()

                    WorkManager.getInstance(application).beginUniqueWork(
                        attendance.oneTimeAttendCheckInEventWorkerName.toString(),
                        ExistingWorkPolicy.APPEND_OR_REPLACE,
                        oneTimeWork
                    ).enqueue()

                    val alarmIntent = Intent(application, AlarmReceiver::class.java).apply {
                        action = RECEIVE_ATTEND_CHECK_IN_ALARM
                        putExtra(ATTENDANCE_ID, attendance.id)
                    }

                    val now = Calendar.getInstance()
                    val targetTime = Calendar.getInstance().apply {
                        time = now.time

                        add(Calendar.DATE, 1)
                        set(Calendar.HOUR_OF_DAY, attendance.hourOfDay)
                        set(Calendar.MINUTE, attendance.minute)
                        set(Calendar.SECOND, 30)
                    }

                    val pendingIntent = PendingIntent.getBroadcast(
                        application,
                        attendance.id.toInt(),
                        alarmIntent,
                        pendingIntentFlagUpdateCurrent
                    )

                    with(alarmManager) {
                        cancel(pendingIntent)
                        AlarmManagerCompat.setExactAndAllowWhileIdle(
                            this,
                            AlarmManager.RTC_WAKEUP,
                            targetTime.timeInMillis,
                            pendingIntent
                        )
                    }
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