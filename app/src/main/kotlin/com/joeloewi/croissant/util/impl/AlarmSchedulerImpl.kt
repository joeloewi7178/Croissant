package com.joeloewi.croissant.util.impl

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import com.joeloewi.croissant.receiver.AlarmReceiver
import com.joeloewi.croissant.util.AlarmScheduler
import com.joeloewi.croissant.util.canScheduleExactAlarmsCompat
import com.joeloewi.croissant.util.pendingIntentFlagUpdateCurrent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
    private val alarmManager: AlarmManager,
    @ApplicationContext private val context: Context
) : AlarmScheduler {

    override fun scheduleCheckInAlarm(
        attendanceId: Long,
        hourOfDay: Int,
        minute: Int
    ) {
        val timeZoneId = ZoneId.systemDefault().id
        val now = ZonedDateTime.now(ZoneId.of(timeZoneId))
        val canExecuteToday =
            (now.hour < hourOfDay) || (now.hour == hourOfDay && now.minute < minute)
        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            attendanceId.toInt(),
            Intent(context, AlarmReceiver::class.java).apply {
                action = AlarmReceiver.RECEIVE_ATTEND_CHECK_IN_ALARM
                putExtra(AlarmReceiver.ATTENDANCE_ID, attendanceId)
            },
            pendingIntentFlagUpdateCurrent
        )
        val targetTime = ZonedDateTime.now(ZoneId.of(timeZoneId))
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

        with(alarmManager) {
            cancel(alarmPendingIntent)
            if (canScheduleExactAlarmsCompat()) {
                AlarmManagerCompat.setExactAndAllowWhileIdle(
                    this,
                    AlarmManager.RTC_WAKEUP,
                    targetTime.toInstant().toEpochMilli(),
                    alarmPendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    targetTime.toInstant().toEpochMilli(),
                    alarmPendingIntent
                )
            }
        }
    }

    override fun cancelCheckInAlarm(attendanceId: Long) {
        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            attendanceId.toInt(),
            Intent(context, AlarmReceiver::class.java).apply {
                action = AlarmReceiver.RECEIVE_ATTEND_CHECK_IN_ALARM
                putExtra(AlarmReceiver.ATTENDANCE_ID, attendanceId)
            },
            pendingIntentFlagUpdateCurrent
        )

        alarmManager.cancel(alarmPendingIntent)
    }
}