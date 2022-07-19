package com.joeloewi.croissant.util

import android.os.Build
import android.widget.TimePicker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun TimePicker(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onCreated: (TimePicker) -> Unit = {},
    hourOfDay: Int,
    minute: Int,
    onHourOfDayChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit
) {
    val (timePicker, onTimePickerChange) = remember { mutableStateOf<TimePicker?>(null) }

    DisposableEffect(timePicker) {
        timePicker?.setOnTimeChangedListener { _, hourOfDay, minute ->
            onHourOfDayChange(hourOfDay)
            onMinuteChange(minute)
        }

        onDispose { timePicker?.setOnTimeChangedListener(null) }
    }

    AndroidView(
        factory = { context ->
            TimePicker(context).apply {
                onCreated(this)
            }.also(onTimePickerChange)
        },
        modifier = modifier
    ) { view ->
        view.isEnabled = enabled

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hourOfDay.takeIf { it != view.hour }?.let(view::setHour)
            minute.takeIf { it != view.minute }?.let(view::setMinute)
        } else {
            hourOfDay.takeIf { it != view.currentHour }?.let(view::setCurrentHour)
            minute.takeIf { it != view.currentMinute }?.let(view::setCurrentMinute)
        }
    }
}

