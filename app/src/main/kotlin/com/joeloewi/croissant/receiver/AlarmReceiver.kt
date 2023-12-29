package com.joeloewi.croissant.receiver

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.joeloewi.croissant.BuildConfig
import com.joeloewi.croissant.domain.usecase.AttendanceUseCase
import com.joeloewi.croissant.util.canScheduleExactAlarmsCompat
import com.joeloewi.croissant.util.pendingIntentFlagUpdateCurrent
import com.joeloewi.croissant.worker.AttendCheckInEventWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    private val _coroutineContext = Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
        Firebase.crashlytics.apply {
            log(this@AlarmReceiver.javaClass.simpleName)
            recordException(throwable)
        }
    }
    private val _processLifecycleScope by lazy { ProcessLifecycleOwner.get().lifecycleScope }

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

    override fun onReceive(p0: Context, p1: Intent) {
        when (p1.action) {
            Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_MY_PACKAGE_REPLACED, AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED -> {
                _processLifecycleScope.launch(_coroutineContext) {
                    getAllOneShotAttendanceUseCase().map { attendance ->
                        async(SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, _ -> }) {
                            attendance.runCatching {
                                val alarmIntent =
                                    Intent(application, AlarmReceiver::class.java).apply {
                                        action = RECEIVE_ATTEND_CHECK_IN_ALARM
                                        putExtra(ATTENDANCE_ID, id)
                                    }

                                val now = ZonedDateTime.now(ZoneId.of(attendance.timezoneId))
                                val canExecuteToday =
                                    (now.hour < hourOfDay) || (now.hour == hourOfDay && now.minute < minute)

                                val targetTime = ZonedDateTime.now(ZoneId.of(attendance.timezoneId))
                                    .plusDays(
                                        if (!canExecuteToday) {
                                            1
                                        } else {
                                            0
                                        }
                                    )
                                    .withHour(hourOfDay)
                                    .withMinute(minute)
                                    .withSecond(30)

                                val pendingIntent = PendingIntent.getBroadcast(
                                    application,
                                    id.toInt(),
                                    alarmIntent,
                                    pendingIntentFlagUpdateCurrent
                                )

                                with(alarmManager) {
                                    cancel(pendingIntent)
                                    if (canScheduleExactAlarmsCompat()) {
                                        AlarmManagerCompat.setExactAndAllowWhileIdle(
                                            this,
                                            AlarmManager.RTC_WAKEUP,
                                            targetTime.toInstant().toEpochMilli(),
                                            pendingIntent
                                        )
                                    } else {
                                        alarmManager.set(
                                            AlarmManager.RTC_WAKEUP,
                                            targetTime.toInstant().toEpochMilli(),
                                            pendingIntent
                                        )
                                    }
                                }
                            }
                        }
                    }.awaitAll()
                }
            }

            RECEIVE_ATTEND_CHECK_IN_ALARM -> {
                val attendanceId = p1.getLongExtra(ATTENDANCE_ID, Long.MIN_VALUE)

                _processLifecycleScope.launch(_coroutineContext) {
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

                    workManager.beginUniqueWork(
                        attendance.oneTimeAttendCheckInEventWorkerName.toString(),
                        ExistingWorkPolicy.APPEND_OR_REPLACE,
                        oneTimeWork
                    ).enqueue()

                    val alarmIntent = Intent(application, AlarmReceiver::class.java).apply {
                        action = RECEIVE_ATTEND_CHECK_IN_ALARM
                        putExtra(ATTENDANCE_ID, attendance.id)
                    }

                    val targetTime = ZonedDateTime.now(ZoneId.of(attendance.timezoneId))
                        .plusDays(1)
                        .withHour(attendance.hourOfDay)
                        .withMinute(attendance.minute)
                        .withSecond(30)

                    val pendingIntent = PendingIntent.getBroadcast(
                        application,
                        attendance.id.toInt(),
                        alarmIntent,
                        pendingIntentFlagUpdateCurrent
                    )

                    with(alarmManager) {
                        cancel(pendingIntent)
                        if (canScheduleExactAlarmsCompat()) {
                            AlarmManagerCompat.setExactAndAllowWhileIdle(
                                this,
                                AlarmManager.RTC_WAKEUP,
                                targetTime.toInstant().toEpochMilli(),
                                pendingIntent
                            )
                        } else {
                            alarmManager.set(
                                AlarmManager.RTC_WAKEUP,
                                targetTime.toInstant().toEpochMilli(),
                                pendingIntent
                            )
                        }
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