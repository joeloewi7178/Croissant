/*
 *    Copyright 2022 joeloewi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.joeloewi.croissant.util

import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import com.joeloewi.croissant.R

fun Context.createNotificationChannels(
    idNamePairs: List<Pair<String, String>> = listOf(
        getString(R.string.attendance_notification_channel_id) to getString(R.string.attendance_notification_channel_name),
        getString(R.string.check_session_notification_channel_id) to getString(R.string.check_session_notification_channel_name),
        getString(R.string.time_zone_changed_notification_channel_id) to getString(R.string.time_zone_changed_notification_channel_name),
        getString(R.string.attendance_foreground_notification_channel_id) to getString(R.string.attendance_foreground_notification_channel_name)
    )
) = idNamePairs.filter { pair ->
    NotificationManagerCompat.from(this).getNotificationChannel(pair.first) == null
}.map { pair ->
    NotificationChannelCompat
        .Builder(
            pair.first,
            NotificationManagerCompat.IMPORTANCE_MAX
        )
        .setName(pair.second)
        .build()
}.let {
    NotificationManagerCompat.from(this).createNotificationChannelsCompat(it)
}