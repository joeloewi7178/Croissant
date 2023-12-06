package com.joeloewi.croissant.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeAndTimePicker(
    modifier: Modifier = Modifier,
    hourOfDay: () -> Int,
    minute: () -> Int,
    onHourOfDayChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }
    val state = rememberTimePickerState(
        initialHour = hourOfDay(),
        initialMinute = minute()
    )
    val configuration = LocalConfiguration.current
    var showingPicker by remember { mutableStateOf(true) }
    val localTime = remember(hourOfDay, minute) {
        LocalTime.now().withHour(hourOfDay()).withMinute(minute())
            .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
    }

    TextButton(
        modifier = modifier,
        onClick = { showTimePicker = true }
    ) {
        Text(text = localTime)
    }

    if (showTimePicker) {
        TimePickerDialog(
            onCancel = { showTimePicker = false },
            onConfirm = {
                val now = ZonedDateTime.now()

                onHourOfDayChange(now.hour)
                onMinuteChange(now.minute)
            },
            toggle = {
                if (configuration.screenHeightDp > 400) {
                    IconButton(onClick = { showingPicker = !showingPicker }) {
                        val icon = if (showingPicker) {
                            Icons.Outlined.Keyboard
                        } else {
                            Icons.Outlined.Schedule
                        }
                        Icon(
                            icon,
                            contentDescription = if (showingPicker) {
                                "Switch to Text Input"
                            } else {
                                "Switch to Touch Input"
                            }
                        )
                    }
                }
            }
        ) {
            if (showingPicker && configuration.screenHeightDp > 400) {
                TimePicker(state = state)
            } else {
                TimeInput(state = state)
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = onCancel
                    ) {
                        Text("Cancel")
                    }
                    TextButton(
                        onClick = onConfirm
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

