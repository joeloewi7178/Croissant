package com.joeloewi.croissant.ui.navigation.main.attendances.screen.createattendance.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.joeloewi.croissant.R
import com.joeloewi.croissant.state.CreateAttendanceState
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.util.LocalHourFormat
import com.joeloewi.croissant.util.TimePicker
import com.joeloewi.croissant.util.dateTimeFormatterPerHourFormat
import java.time.ZonedDateTime

@Composable
fun SetTime(
    modifier: Modifier,
    createAttendanceState: CreateAttendanceState,
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            FilledTonalButton(
                modifier = Modifier
                    .padding(horizontal = DefaultDp)
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                onClick = createAttendanceState::onNextButtonClick
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        space = DefaultDp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = Icons.Default.Done.name
                    )
                    Text(text = stringResource(id = R.string.completed))
                }
            }
        },
        contentWindowInsets = WindowInsets.systemBars.exclude(WindowInsets.statusBars)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = DefaultDp),
            verticalArrangement = Arrangement.spacedBy(space = DefaultDp)
        ) {
            Text(
                text = stringResource(id = R.string.select_scheduled_time),
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = stringResource(id = R.string.type_time),
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = stringResource(id = R.string.select_scheduled_time_by_using_dial),
                style = MaterialTheme.typography.bodyMedium
            )

            TimePickerWithState(createAttendanceState = createAttendanceState)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.first_execution),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                FirstExecutionTime(createAttendanceState = createAttendanceState)
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(DefaultDp),
                ) {
                    Icon(
                        modifier = Modifier.padding(DefaultDp),
                        imageVector = Icons.Default.Star,
                        contentDescription = Icons.Default.Star.name
                    )
                    Text(
                        modifier = Modifier.padding(DefaultDp),
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(stringResource(id = R.string.note))
                                append(": ")
                            }
                            append(stringResource(id = R.string.first_execution_is_for_reference))
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun TimePickerWithState(
    createAttendanceState: CreateAttendanceState
) {
    TimePicker(
        modifier = Modifier.fillMaxWidth(),
        hourOfDay = createAttendanceState.hourOfDay,
        minute = createAttendanceState.minute,
        onHourOfDayChange = remember(createAttendanceState) {
            createAttendanceState::onHourOfDayChange
        },
        onMinuteChange = remember(createAttendanceState) { createAttendanceState::onMinuteChange }
    )
}

@Composable
private fun FirstExecutionTime(
    createAttendanceState: CreateAttendanceState
) {
    val hourOfDay = createAttendanceState.hourOfDay
    val minute = createAttendanceState.minute
    val currentTickPerSecond = createAttendanceState.tickPerSecond
    val canExecuteToday by remember(currentTickPerSecond, hourOfDay, minute) {
        derivedStateOf {
            (currentTickPerSecond.hour < hourOfDay) || (currentTickPerSecond.hour == hourOfDay && currentTickPerSecond.minute < minute)
        }
    }
    val today = stringResource(id = R.string.today)
    val tomorrow = stringResource(id = R.string.tomorrow)

    val todayOrTomorrow by remember(canExecuteToday) {
        derivedStateOf {
            if (canExecuteToday) {
                today
            } else {
                tomorrow
            }
        }
    }

    val hourFormat = LocalHourFormat.current
    val formattedTime by remember(
        hourOfDay,
        minute,
        hourFormat
    ) {
        derivedStateOf {
            ZonedDateTime.now()
                .withHour(hourOfDay)
                .withMinute(minute)
                .format(
                    dateTimeFormatterPerHourFormat(hourFormat)
                )
        }
    }
    val firstExecutionTime by remember(todayOrTomorrow, formattedTime) {
        derivedStateOf {
            "$todayOrTomorrow $formattedTime"
        }
    }

    Text(
        text = firstExecutionTime,
        style = MaterialTheme.typography.headlineMedium
    )
}