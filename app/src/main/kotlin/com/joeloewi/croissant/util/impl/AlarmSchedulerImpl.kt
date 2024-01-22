package com.joeloewi.croissant.util.impl

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import com.joeloewi.croissant.domain.entity.Attendance
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
        attendance: Attendance,
        scheduleForTomorrow: Boolean
    ) {
        val timeZoneId = ZoneId.of(attendance.timezoneId)
        val now = ZonedDateTime.now(timeZoneId)
        val alarmPendingIntent = createCheckInAlarmPendingIntent(attendance.id)
        val target = now.withHour(attendance.hourOfDay)
            .withMinute(attendance.minute)
            .withSecond(30)
            .withNano(0)
        val canExecuteToday = now.isBefore(target)
        val targetTimeMillis = target.run {
            if (!scheduleForTomorrow) {
                if (canExecuteToday) {
                    return@run this
                }
                return@run plusDays(1)
            }
            return@run plusDays(1)
        }.toInstant().toEpochMilli()

        with(alarmManager) {
            if (canScheduleExactAlarmsCompat()) {
                AlarmManagerCompat.setExactAndAllowWhileIdle(
                    this,
                    AlarmManager.RTC_WAKEUP,
                    targetTimeMillis,
                    alarmPendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    targetTimeMillis,
                    alarmPendingIntent
                )
            }
        }
    }

    override fun cancelCheckInAlarm(attendanceId: Long) {
        alarmManager.cancel(createCheckInAlarmPendingIntent(attendanceId))
    }

    private fun createCheckInAlarmPendingIntent(
        attendanceId: Long
    ) = PendingIntent.getBroadcast(
        context,
        attendanceId.toInt(),
        Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.RECEIVE_ATTEND_CHECK_IN_ALARM
            putExtra(AlarmReceiver.ATTENDANCE_ID, attendanceId)
        },
        pendingIntentFlagUpdateCurrent
    )
}