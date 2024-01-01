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
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.flowWithLifecycle
import com.joeloewi.croissant.R
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.util.LocalHourFormat
import com.joeloewi.croissant.util.TimeAndTimePicker
import com.joeloewi.croissant.util.dateTimeFormatterPerHourFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import java.time.ZonedDateTime

@Composable
fun SetTime(
    modifier: Modifier = Modifier,
    hourOfDay: () -> Int,
    minute: () -> Int,
    onHourOfDayChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    onNextButtonClick: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            FilledTonalButton(
                modifier = Modifier
                    .padding(horizontal = DefaultDp)
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                onClick = onNextButtonClick
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

            TimePickerWithState(
                hourOfDay = hourOfDay,
                minute = minute,
                onHourOfDayChange = onHourOfDayChange,
                onMinuteChange = onMinuteChange
            )

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
                FirstExecutionTime(
                    hourOfDay = hourOfDay,
                    minute = minute
                )
            }
        }
    }
}

@Composable
private fun TimePickerWithState(
    hourOfDay: () -> Int,
    minute: () -> Int,
    onHourOfDayChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit
) {
    TimeAndTimePicker(
        modifier = Modifier.fillMaxWidth(),
        hourOfDay = hourOfDay,
        minute = minute,
        onHourOfDayChange = onHourOfDayChange,
        onMinuteChange = onMinuteChange
    )
}

@Composable
private fun FirstExecutionTime(
    hourOfDay: () -> Int,
    minute: () -> Int
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var canExecuteToday by remember { mutableStateOf(false) }
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
                .withHour(hourOfDay())
                .withMinute(minute())
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

    LaunchedEffect(Unit) {
        flow {
            while (currentCoroutineContext().isActive) {
                emit(ZonedDateTime.now())
                delay(1000)
            }
        }.catch {}.flowWithLifecycle(lifecycleOwner.lifecycle).flowOn(Dispatchers.IO).collect {
            canExecuteToday =
                (it.hour < hourOfDay()) || (it.hour == hourOfDay() && it.minute < minute())
        }
    }

    Text(
        text = firstExecutionTime,
        style = MaterialTheme.typography.headlineMedium
    )
}