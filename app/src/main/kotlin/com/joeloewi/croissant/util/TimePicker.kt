package com.joeloewi.croissant.util

import android.os.Build
import android.widget.TimePicker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import java.time.ZonedDateTime

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
    val timePickerState = rememberTimePickerState(
        hourOfDay = hourOfDay,
        minute = minute,
        onHourOfDayChange = onHourOfDayChange,
        onMinuteChange = onMinuteChange
    )
    val hourFormat = LocalHourFormat.current

    AndroidView(
        factory = { androidViewContext ->
            TimePicker(androidViewContext).apply {
                onCreated(this)

                isEnabled = enabled
                setIs24HourView(hourFormat == HourFormat.TwentyFourHour)

                setOnTimeChangedListener { _, hourOfDay, minute ->
                    timePickerState.onHourOfDayChange(hourOfDay)
                    timePickerState.onMinuteChange(minute)
                }
            }
        },
        modifier = modifier
    ) { view ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePickerState.hourOfDay.takeIf { it != view.hour }?.let(view::setHour)
            timePickerState.minute.takeIf { it != view.minute }?.let(view::setMinute)
        } else {
            timePickerState.hourOfDay.takeIf { it != view.currentHour }?.let(view::setCurrentHour)
            timePickerState.minute.takeIf { it != view.currentMinute }?.let(view::setCurrentMinute)
        }
    }
}

@Composable
fun rememberTimePickerState(
    hourOfDay: Int = ZonedDateTime.now().hour,
    minute: Int = ZonedDateTime.now().minute,
    onHourOfDayChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit
) = remember(hourOfDay, minute, onHourOfDayChange, onMinuteChange) {
    TimePickerState(hourOfDay, minute, onHourOfDayChange, onMinuteChange)
}

@Stable
class TimePickerState(
    hourOfDay: Int = ZonedDateTime.now().hour,
    minute: Int = ZonedDateTime.now().minute,
    onHourOfDayChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit
) {
    var hourOfDay: Int by mutableStateOf(hourOfDay)
    var minute: Int by mutableStateOf(minute)
    var onHourOfDayChange: ((Int) -> Unit) by mutableStateOf(onHourOfDayChange)
    var onMinuteChange: ((Int) -> Unit) by mutableStateOf(onMinuteChange)
}

