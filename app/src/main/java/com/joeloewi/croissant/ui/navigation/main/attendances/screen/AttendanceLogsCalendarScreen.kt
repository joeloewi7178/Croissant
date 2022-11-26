package com.joeloewi.croissant.ui.navigation.main.attendances.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.joeloewi.croissant.R
import com.joeloewi.croissant.state.AttendanceLogsCalendarState
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.state.rememberAttendanceLogsCalendarState
import com.joeloewi.croissant.ui.theme.*
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.generateCalendarDays
import com.joeloewi.croissant.util.isCompactWindowSize
import com.joeloewi.croissant.util.navigationIconButton
import com.joeloewi.croissant.viewmodel.AttendanceLogsCalendarViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.format.DateTimeFormatter

@ExperimentalMaterial3WindowSizeClassApi
@ExperimentalPagerApi
@ExperimentalLayoutApi
@ExperimentalLifecycleComposeApi
@ExperimentalMaterial3Api
@Composable
fun AttendanceLogsCalendarScreen(
    navController: NavController,
    attendanceLogsCalendarViewModel: AttendanceLogsCalendarViewModel = hiltViewModel()
) {
    val attendanceLogsCalendarState = rememberAttendanceLogsCalendarState(
        navController = navController,
        attendanceLogsCalendarViewModel = attendanceLogsCalendarViewModel
    )

    AttendanceLogsCalendarContent(
        attendanceLogsCalendarState = attendanceLogsCalendarState,
    )
}

@ExperimentalMaterial3WindowSizeClassApi
@ExperimentalPagerApi
@ExperimentalLayoutApi
@ExperimentalLifecycleComposeApi
@ExperimentalMaterial3Api
@Composable
private fun AttendanceLogsCalendarContent(
    attendanceLogsCalendarState: AttendanceLogsCalendarState,
) {
    val context = LocalContext.current
    val deleteAllState = attendanceLogsCalendarState.deleteAllState
    val pagerState = attendanceLogsCalendarState.pagerState
    val year = attendanceLogsCalendarState.year
    val (expanded, onExpandedChange) = rememberSaveable { mutableStateOf(false) }
    val years by remember(Year.now()) {
        derivedStateOf {
            (1900..Year.now().value).reversed().map { Year.of(it) }
        }
    }

    LaunchedEffect(deleteAllState) {
        when (deleteAllState) {
            is Lce.Content -> {
                val rowCount = deleteAllState.content
                if (rowCount != -1) {
                    attendanceLogsCalendarState.snackbarHostState.showSnackbar(
                        context.getString(
                            R.string.logs_deleted,
                            rowCount
                        )
                    )
                }
            }
            else -> {

            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = attendanceLogsCalendarState.snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.execution_log))
                },
                navigationIcon = navigationIconButton(
                    previousBackStackEntry = attendanceLogsCalendarState.previousBackStackEntry,
                    onClick = attendanceLogsCalendarState::onNavigateUp
                ),
                actions = {
                    IconButton(
                        onClick = {
                            attendanceLogsCalendarState.showDeleteConfirmationDialog(true)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = Icons.Default.DeleteSweep.name
                        )
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        onExpandedChange(it)
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.menuAnchor(),
                            text = year.value.toString(),
                            style = MaterialTheme.typography.displayMedium,
                        )

                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }

                    ExposedDropdownMenu(
                        modifier = Modifier.exposedDropdownSize(),
                        expanded = expanded,
                        onDismissRequest = { onExpandedChange(false) }
                    ) {
                        years.forEach { year ->
                            key(year.value) {
                                DropdownMenuItem(
                                    text = {
                                        Text(text = "${year.value}")
                                    },
                                    onClick = {
                                        attendanceLogsCalendarState.setYear(year)
                                        onExpandedChange(false)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Row {
                HorizontalPager(
                    modifier = Modifier.fillMaxSize(),
                    state = pagerState,
                    key = {
                        year.atMonth(it + 1).format(DateTimeFormatter.ofPattern("yyyy-MM"))
                    },
                    count = Month.values().size
                ) { page ->

                    MonthPage(
                        page = page,
                        year = { year },
                        dayLogCount = attendanceLogsCalendarState::getCountByDate,
                        onClickDay = attendanceLogsCalendarState::onClickDay
                    )
                }
            }

            if (attendanceLogsCalendarState.isShowingDeleteConfirmationDialog) {
                AlertDialog(
                    onDismissRequest = {
                        attendanceLogsCalendarState.showDeleteConfirmationDialog(false)
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                attendanceLogsCalendarState.onDeleteAll()
                            }
                        ) {
                            Text(text = stringResource(id = R.string.confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                attendanceLogsCalendarState.showDeleteConfirmationDialog(false)
                            }
                        ) {
                            Text(text = stringResource(id = R.string.dismiss))
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = Icons.Default.Warning.name
                        )
                    },
                    title = {
                        Text(text = stringResource(id = R.string.caution))
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.all_execution_log_will_be_deleted),
                            textAlign = TextAlign.Center
                        )
                    }
                )
            }
        }
    }
}

@ExperimentalPagerApi
@ExperimentalMaterial3WindowSizeClassApi
@ExperimentalLifecycleComposeApi
@Composable
private fun MonthPage(
    page: Int,
    year: () -> Year,
    dayLogCount: (Year, Month, Int) -> Flow<Pair<Long, Long>>,
    onClickDay: (localDate: String) -> Unit
) {
    val updatedOnDayClick by rememberUpdatedState(newValue = onClickDay)
    val month = remember(page) { Month.values()[page] }
    val totalDays =
        remember(year().atMonth(month)) {
            year().atMonth(month).generateCalendarDays()
        }

    Column(
        modifier = Modifier
            .padding(DefaultDp)
            .fillMaxSize()
    ) {
        Row {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = DefaultDp),
                text = month.value.toString(),
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center,
            )
        }

        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(7)
        ) {
            itemsIndexed(
                items = totalDays,
                key = { index, _ ->
                    year().atMonth(month)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM")) + "[$index]"
                }
            ) { _, day ->

                DayGridItem(
                    year = year,
                    month = month,
                    day = day,
                    dayLogCount = dayLogCount,
                    onClickDay = updatedOnDayClick
                )
            }
        }
    }
}

@ExperimentalPagerApi
@ExperimentalMaterial3WindowSizeClassApi
@ExperimentalLifecycleComposeApi
@Composable
private fun DayGridItem(
    year: () -> Year,
    month: Month,
    day: Int,
    dayLogCount: (Year, Month, Int) -> Flow<Pair<Long, Long>>,
    onClickDay: (localDate: String) -> Unit
) {
    val windowSizeClass = calculateWindowSizeClass(activity = LocalActivity.current)
    val updatedOnClick by rememberUpdatedState(newValue = onClickDay)

    Column(
        modifier = Modifier
            .padding(1.dp)
            .fillMaxWidth()
            .aspectRatio(
                if (windowSizeClass.isCompactWindowSize()) {
                    0.5f
                } else {
                    1f
                }
            )
    ) {
        if (day != 0) {
            val logCount by remember(year(), month, day) {
                dayLogCount(year(), month, day)
            }.collectAsStateWithLifecycle(initialValue = 0L to 0L, context = Dispatchers.IO)
            val colorScheme = MaterialTheme.colorScheme

            val primaryColor = remember(colorScheme) {
                colorScheme.primary
            }
            val date = remember(year(), month, day) {
                year().atMonth(month).atDay(day)
            }

            val isToday by remember(LocalDate.now(), date) {
                derivedStateOf {
                    LocalDate.now() == date
                }
            }

            val circleModifier by remember(isToday) {
                derivedStateOf {
                    if (isToday) {
                        Modifier.drawBehind {
                            drawCircle(
                                color = primaryColor
                            )
                        }
                    } else {
                        Modifier
                    }
                }
            }

            val dayTextColor = remember(isToday, colorScheme) {
                if (isToday) {
                    colorScheme.onPrimary
                } else {
                    Color.Unspecified
                }
            }

            Row {
                Text(
                    modifier = circleModifier.fillMaxWidth(),
                    text = "$day",
                    style = MaterialTheme.typography.labelMedium,
                    color = dayTextColor,
                    textAlign = TextAlign.Center
                )
            }

            Row(
                modifier = Modifier
                    .weight(1f)
            ) {
                val backgroundColor = with(
                    logCount
                ) {
                    when {
                        first > 0L && second > 0 -> {
                            warningContainerColor
                        }

                        first > 0 -> {
                            successContainerColor
                        }

                        second > 0 -> {
                            MaterialTheme.colorScheme.errorContainer
                        }

                        else -> {
                            Color.Unspecified
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            enabled = with(logCount) {
                                first > 0 || second > 0
                            },
                        ) {
                            updatedOnClick(
                                date.format(
                                    DateTimeFormatter.ISO_LOCAL_DATE
                                )
                            )
                        }
                        .background(backgroundColor),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (logCount.first > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                HalfDp
                            )
                        ) {
                            Icon(
                                modifier = Modifier.size(
                                    DoubleDp
                                ),
                                imageVector = Icons.Default.Check,
                                contentDescription = Icons.Default.Check.name,
                                tint = onSuccessContainerColor
                            )

                            Text(
                                text = "${logCount.first}",
                                color = onSuccessContainerColor,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    if (logCount.second > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                HalfDp
                            )
                        ) {
                            Icon(
                                modifier = Modifier.size(
                                    DoubleDp
                                ),
                                imageVector = Icons.Default.Error,
                                contentDescription = Icons.Default.Error.name,
                                tint = MaterialTheme.colorScheme.error
                            )

                            Text(
                                text = "${logCount.second}",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}