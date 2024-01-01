package com.joeloewi.croissant.util

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.View
import android.widget.RemoteViews
import androidx.core.os.bundleOf
import com.joeloewi.croissant.R
import com.joeloewi.croissant.receiver.ResinStatusWidgetProvider
import com.joeloewi.croissant.service.RemoteViewsFactoryService
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun createErrorDueToPowerSaveModeRemoteViews(
    context: Context,
    appWidgetId: Int
) = RemoteViews(
    context.packageName,
    R.layout.widget_resin_status_battery_optimization_enabled
).apply {
    setOnClickPendingIntent(
        R.id.button_retry,
        PendingIntent.getBroadcast(
            context,
            appWidgetId,
            Intent(
                context,
                ResinStatusWidgetProvider::class.java
            ).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

                putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    intArrayOf(appWidgetId)
                )
            },
            pendingIntentFlagUpdateCurrent
        )
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        setOnClickPendingIntent(
            R.id.button_change_setting,
            PendingIntent.getActivity(
                context,
                appWidgetId,
                Intent(
                    Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                ),
                pendingIntentFlagUpdateCurrent
            )
        )
    } else {
        setViewVisibility(R.id.button_change_setting, View.INVISIBLE)
    }
}

fun createUnknownErrorRemoteViews(
    context: Context,
    appWidgetId: Int
) = RemoteViews(
    context.packageName,
    R.layout.widget_resin_status_error
).apply {
    setOnClickPendingIntent(
        R.id.button_retry,
        PendingIntent.getBroadcast(
            context,
            appWidgetId,
            Intent(
                context,
                ResinStatusWidgetProvider::class.java
            ).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

                putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    intArrayOf(appWidgetId)
                )
            },
            pendingIntentFlagUpdateCurrent
        )
    )
}

fun createLoadingRemoteViews(
    context: Context,
) = RemoteViews(
    context.packageName,
    R.layout.widget_resin_status_loading
)

fun createContentRemoteViews(
    context: Context,
    appWidgetId: Int,
    resinStatuses: List<ResinStatus>
) = RemoteViews(
    context.packageName,
    R.layout.widget_resin_status
).apply {
    //set timestamp
    val dateTimeFormatter =
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)

    val localDateTime =
        Instant.now()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    val readableTimestamp = dateTimeFormatter.format(localDateTime)

    setTextViewText(R.id.widget_timestamp, readableTimestamp)

    //set click listener
    setOnClickPendingIntent(
        R.id.widget_refresh,
        PendingIntent.getBroadcast(
            context,
            appWidgetId,
            Intent(
                context,
                ResinStatusWidgetProvider::class.java
            ).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

                putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    intArrayOf(appWidgetId)
                )
            },
            pendingIntentFlagUpdateCurrent
        )
    )

    //set resin status
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val items = RemoteViews.RemoteCollectionItems.Builder()
            .apply {
                resinStatuses.forEach {
                    addItem(
                        it.id,
                        RemoteViews(
                            context.packageName,
                            android.R.layout.two_line_list_item
                        ).apply {
                            setTextViewText(
                                android.R.id.text1,
                                it.nickname
                            )
                            setTextViewText(
                                android.R.id.text2,
                                "${it.currentResin} / ${it.maxResin}"
                            )
                        }
                    )
                }
            }
            .setViewTypeCount(1)
            .setHasStableIds(false)
            .build()

        setRemoteAdapter(
            R.id.resin_statuses,
            items
        )
    } else {
        val serviceIntent = Intent(
            context,
            RemoteViewsFactoryService::class.java
        )

        val extrasBundle = bundleOf().apply {
            putParcelableArrayList(
                ListRemoteViewsFactory.RESIN_STATUSES,
                ArrayList(resinStatuses)
            )
        }

        serviceIntent.putExtra(ListRemoteViewsFactory.BUNDLE, extrasBundle)

        setRemoteAdapter(
            R.id.resin_statuses, serviceIntent
        )
    }
}