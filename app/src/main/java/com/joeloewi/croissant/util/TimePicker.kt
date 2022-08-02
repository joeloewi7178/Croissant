package com.joeloewi.croissant.util

import android.os.Build
import android.widget.TimePicker
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi

@ExperimentalLifecycleComposeApi
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
    val is24HourFormat = LocalIs24HourFormat.current
    val (timePicker, onTimePickerChange) = remember { mutableStateOf<TimePicker?>(null) }
    val currentOnHourOfDayChange by rememberUpdatedState(newValue = onHourOfDayChange)
    val currentOnMinuteChange by rememberUpdatedState(newValue = onMinuteChange)

    DisposableEffect(timePicker) {
        timePicker?.setOnTimeChangedListener { _, hourOfDay, minute ->
            currentOnHourOfDayChange(hourOfDay)
            currentOnMinuteChange(minute)
        }

        onDispose { timePicker?.setOnTimeChangedListener(null) }
    }

    AndroidView(
        factory = { androidViewContext ->
            TimePicker(androidViewContext).apply(onCreated).also(onTimePickerChange)
        },
        modifier = modifier
    ) { view ->
        view.apply {
            isEnabled = enabled
            setIs24HourView(is24HourFormat)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hourOfDay.takeIf { it != view.hour }?.let(view::setHour)
            minute.takeIf { it != view.minute }?.let(view::setMinute)
        } else {
            hourOfDay.takeIf { it != view.currentHour }?.let(view::setCurrentHour)
            minute.takeIf { it != view.currentMinute }?.let(view::setCurrentMinute)
        }
    }
}

