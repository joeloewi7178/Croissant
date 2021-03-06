package com.joeloewi.croissant.ui.navigation.main.attendances.screen.createattendance.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joeloewi.croissant.R
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.util.TimePicker
import com.joeloewi.croissant.util.is24HourFormat
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

@ExperimentalLifecycleComposeApi
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

            TimePicker(
                modifier = Modifier.fillMaxWidth(),
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

            val canExecuteToday by remember(tickerCalendar, hourOfDay, minute) {
                derivedStateOf {
                    (tickerCalendar[Calendar.HOUR_OF_DAY] < hourOfDay) || (tickerCalendar[Calendar.HOUR_OF_DAY] == hourOfDay && tickerCalendar[Calendar.MINUTE] < minute)
                }
            }

            val todayOrTomorrow by rememberUpdatedState(
                newValue = if (canExecuteToday) {
                    stringResource(id = R.string.today)
                } else {
                    stringResource(id = R.string.tomorrow)
                }
            )

            val context = LocalContext.current
            val is24HourFormat by context.is24HourFormat().collectAsStateWithLifecycle()
            val time by remember(hourOfDay, minute) {
                derivedStateOf {
                    "${hourOfDay.toString().padStart(2, '0')} : ${
                        minute.toString().padStart(2, '0')
                    }"
                }
            }
            val formattedTime by remember(is24HourFormat, time) {
                derivedStateOf {
                    if (is24HourFormat) {
                        time
                    } else {
                        LocalTime.parse(
                            time,
                            DateTimeFormatter.ofPattern(
                                "HH : mm",
                            )
                        ).format(DateTimeFormatter.ofPattern("a hh : mm"))
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$todayOrTomorrow $formattedTime",
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