package com.joeloewi.croissant.ui.navigation.main.attendances.screen.createattendance.composable

import android.os.Build
import android.widget.TimePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.viewinterop.AndroidView
import com.joeloewi.croissant.R
import com.joeloewi.croissant.ui.theme.DefaultDp
import kotlinx.coroutines.ObsoleteCoroutinesApi
import java.util.*

@ObsoleteCoroutinesApi
@ExperimentalMaterial3Api
@Composable
fun SetTime(
    hourOfDay: Int,
    minute: Int,
    tickerCalendar: Calendar,
    onNextButtonClick: () -> Unit,
    onHourOfDayChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit
) {
    val (timePicker, onTimePickerChange) = remember {
        mutableStateOf<TimePicker?>(
            null
        )
    }

    DisposableEffect(timePicker) {
        timePicker?.setOnTimeChangedListener { _, hourOfDay, minute ->
            onHourOfDayChange(hourOfDay)
            onMinuteChange(minute)
        }

        onDispose {
            timePicker?.setOnTimeChangedListener(null)
        }
    }

    Scaffold(
        bottomBar = {
            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface),
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .fillMaxSize()
                .padding(innerPadding),
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

            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { androidViewContext ->
                    TimePicker(androidViewContext).apply {
                        setIs24HourView(true)
                    }.also(onTimePickerChange)
                }
            ) { view ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    hourOfDay.takeIf { it != view.hour }?.let(view::setHour)
                    minute.takeIf { it != view.minute }?.let(view::setMinute)
                } else {
                    hourOfDay.takeIf { it != view.currentHour }?.let(view::setCurrentHour)
                    minute.takeIf { it != view.currentMinute }?.let(view::setCurrentMinute)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.first_execution),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            val canExecuteToday =
                (tickerCalendar[Calendar.HOUR_OF_DAY] < hourOfDay) || (tickerCalendar[Calendar.HOUR_OF_DAY] == hourOfDay && tickerCalendar[Calendar.MINUTE] < minute)

            val todayOrTomorrow = if (canExecuteToday) {
                stringResource(id = R.string.today)
            } else {
                stringResource(id = R.string.tomorrow)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$todayOrTomorrow ${
                        hourOfDay.toString().padStart(2, '0')
                    } : ${minute.toString().padStart(2, '0')}",
                    style = MaterialTheme.typography.headlineMedium
                )
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