package com.joeloewi.croissant.ui.navigation.attendances.screen.createattendance.composable

import android.os.Build
import android.widget.TimePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.joeloewi.croissant.rememberFlow
import com.joeloewi.croissant.viewmodel.CreateAttendanceViewModel
import kotlinx.coroutines.ObsoleteCoroutinesApi
import java.util.*

@ObsoleteCoroutinesApi
@ExperimentalMaterial3Api
@Composable
fun SetTime(
    createAttendanceViewModel: CreateAttendanceViewModel = hiltViewModel(),
    onNextButtonClick: () -> Unit
) {
    val hourOfDay by rememberFlow(flow = createAttendanceViewModel.hourOfDay).collectAsState(initial = Calendar.getInstance()[Calendar.HOUR_OF_DAY])
    val minute by rememberFlow(flow = createAttendanceViewModel.minute).collectAsState(initial = Calendar.getInstance()[Calendar.MINUTE])
    val tickerCalendar by rememberFlow(flow = createAttendanceViewModel.tickerCalendar).collectAsState(
        initial = Calendar.getInstance()
    )
    val (timePicker, onTimePickerChange) = remember { mutableStateOf<TimePicker?>(null) }

    DisposableEffect(timePicker) {
        timePicker?.setOnTimeChangedListener { _, hourOfDay, minute ->
            createAttendanceViewModel.apply {
                setHourOfDay(hourOfDay)
                setMinute(minute)
            }
        }

        onDispose {
            timePicker?.setOnTimeChangedListener(null)
        }
    }

    Scaffold(
        bottomBar = {
            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNextButtonClick
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Done,
                        contentDescription = Icons.Outlined.Done.name
                    )
                    Text(text = "시간 설정 완료")
                }
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(space = 16.dp)
        ) {
            item {
                Text(
                    text = "실행할 시간 정하기",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            item {
                Text(
                    text = "시간 입력하기",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item {
                Text(
                    text = "아래의 다이얼로 출석 작업을 매일 언제 실행할지 지정해주세요.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item {
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
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "최초 실행 시점",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                val canExecuteToday =
                    (tickerCalendar[Calendar.HOUR_OF_DAY] < hourOfDay) || (tickerCalendar[Calendar.HOUR_OF_DAY] == hourOfDay && tickerCalendar[Calendar.MINUTE] < minute)

                val todayOrTomorrow = if (canExecuteToday) {
                    "오늘"
                } else {
                    "내일"
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
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(all = 8.dp),
                    ) {
                        Icon(
                            modifier = Modifier.padding(all = 8.dp),
                            imageVector = Icons.Outlined.Star,
                            contentDescription = Icons.Outlined.Star.name
                        )
                        Text(
                            modifier = Modifier.padding(all = 8.dp),
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("참고: ")
                                }
                                append("위 최초 실행 시점은 출석 작업 작성완료 시점으로 참고용입니다.")
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}