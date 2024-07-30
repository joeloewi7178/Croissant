package com.joeloewi.croissant.ui.navigation.main.attendances.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.joeloewi.croissant.R
import com.joeloewi.croissant.core.model.LoggableWorker
import com.joeloewi.croissant.core.model.ResultCount
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.DoubleDp
import com.joeloewi.croissant.ui.theme.HalfDp
import com.joeloewi.croissant.ui.theme.onSuccessContainerColor
import com.joeloewi.croissant.ui.theme.successContainerColor
import com.joeloewi.croissant.ui.theme.warningContainerColor
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.ProgressDialog
import com.joeloewi.croissant.util.generateCalendarDays
import com.joeloewi.croissant.util.navigationIconButton
import com.joeloewi.croissant.util.useNavRail
import com.joeloewi.croissant.viewmodel.AttendanceLogsCalendarViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AttendanceLogsCalendarScreen(
    attendanceLogsCalendarViewModel: AttendanceLogsCalendarViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit,
    onClickDay: (attendanceId: Long, loggableWorker: LoggableWorker, localDate: String) -> Unit
) {
    val state by attendanceLogsCalendarViewModel.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val pagerState = rememberPagerState {
        with(state.startToEnd) {
            ChronoUnit.MONTHS.between(first, second) + 1
        }.toInt()
    }

    var showLoadingDialog by remember { mutableStateOf(false) }

    attendanceLogsCalendarViewModel.collectSideEffect {
        when (it) {
            is AttendanceLogsCalendarViewModel.SideEffect.NavigateToDay -> {
                onClickDay(it.attendanceId, it.loggableWorker, it.localDate)
            }

            is AttendanceLogsCalendarViewModel.SideEffect.ShowDeleteCompleteSnackbar -> {
                coroutineScope.launch {
                    if (it.count > 0) {
                        snackbarHostState.showSnackbar(
                            context.getString(
                                R.string.logs_deleted,
                                it.count
                            )
                        )
                    }
                }
            }

            AttendanceLogsCalendarViewModel.SideEffect.HideLoadingDialog -> {
                showLoadingDialog = true
            }

            AttendanceLogsCalendarViewModel.SideEffect.ShowLoadingDialog -> {
                showLoadingDialog = false
            }
        }
    }

    AttendanceLogsCalendarContent(
        state = state,
        pagerState = pagerState,
        snackbarHostState = snackbarHostState,
        showLoadingDialog = showLoadingDialog,
        onDeleteAll = attendanceLogsCalendarViewModel::deleteAll,
        onNavigateUp = onNavigateUp,
        onClickDay = attendanceLogsCalendarViewModel::onClickDay,
        onShowConfirmDeleteDialogChange = attendanceLogsCalendarViewModel::onShowConfirmDeleteDialogChange
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun AttendanceLogsCalendarContent(
    state: AttendanceLogsCalendarViewModel.State,
    pagerState: PagerState,
    snackbarHostState: SnackbarHostState,
    showLoadingDialog: Boolean,
    onDeleteAll: () -> Unit,
    onNavigateUp: () -> Unit,
    onClickDay: (localDate: String) -> Unit,
    onShowConfirmDeleteDialogChange: (showConfirmDeleteDialog: Boolean) -> Unit
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.execution_log))
                },
                navigationIcon = LocalViewModelStoreOwner.current.navigationIconButton(
                    onClick = onNavigateUp
                ),
                actions = {
                    IconButton(
                        onClick = {
                            onShowConfirmDeleteDialogChange(true)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = Icons.Default.DeleteSweep.name
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            Row {
                HorizontalPager(
                    modifier = Modifier.fillMaxSize(),
                    state = pagerState,
                    key = {
                        state.startToEnd.second.minusMonths(it.toLong())
                            .format(DateTimeFormatter.ofPattern("yyyy-MM"))
                    },
                    reverseLayout = true
                ) { page ->

                    MonthPage(
                        yearMonth = with(state.startToEnd.second.minusMonths(page.toLong())) {
                            Year.of(year).atMonth(month)
                        },
                        resultCounts = state.resultCounts,
                        onClickDay = onClickDay
                    )
                }
            }

            if (state.showConfirmDeleteDialog) {
                AlertDialog(
                    onDismissRequest = {
                        onShowConfirmDeleteDialogChange(false)
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onDeleteAll()
                            }
                        ) {
                            Text(text = stringResource(id = R.string.confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                onShowConfirmDeleteDialogChange(false)
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

    if (showLoadingDialog) {
        ProgressDialog(
            title = {
                Text(text = stringResource(id = R.string.loading))
            }
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun MonthPage(
    yearMonth: YearMonth,
    resultCounts: ImmutableList<ResultCount>,
    onClickDay: (localDate: String) -> Unit
) {
    val updatedOnDayClick by rememberUpdatedState(newValue = onClickDay)
    val totalDays = remember(yearMonth) { yearMonth.generateCalendarDays() }
    val windowSizeClass = calculateWindowSizeClass(activity = LocalActivity.current)

    Column(
        modifier = Modifier
            .padding(horizontal = DefaultDp)
            .fillMaxSize()
    ) {
        Row {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = DefaultDp),
                text = yearMonth.toString(),
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
                    yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")) + "[$index]"
                }
            ) { _, day ->

                DayGridItem(
                    windowSizeClass = windowSizeClass,
                    yearMonth = yearMonth,
                    day = day,
                    resultCounts = resultCounts,
                    onClickDay = updatedOnDayClick
                )
            }
        }
    }
}

@Composable
private fun DayGridItem(
    windowSizeClass: WindowSizeClass,
    yearMonth: YearMonth,
    day: Int,
    resultCounts: ImmutableList<ResultCount>,
    onClickDay: (localDate: String) -> Unit
) {
    val updatedOnClick by rememberUpdatedState(newValue = onClickDay)

    Column(
        modifier = Modifier
            .padding(1.dp)
            .fillMaxWidth()
            .aspectRatio(
                if (!windowSizeClass.useNavRail()) {
                    0.5f
                } else {
                    1f
                }
            )
    ) {
        if (day != 0) {
            val date = remember(yearMonth, day) {
                yearMonth.atDay(day)
            }
            val logCount = resultCounts.find {
                it.date == date.toString()
            } ?: ResultCount(date = date.toString())
            val colorScheme = MaterialTheme.colorScheme
            val primaryColor = remember(colorScheme) {
                colorScheme.primary
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
                    .clip(MaterialTheme.shapes.extraSmall)
            ) {
                val backgroundColor = with(
                    logCount
                ) {
                    when {
                        successCount > 0L && failureCount > 0 -> {
                            warningContainerColor
                        }

                        successCount > 0 -> {
                            successContainerColor
                        }

                        failureCount > 0 -> {
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
                                successCount > 0 || failureCount > 0
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
                    if (logCount.successCount > 0) {
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
                                text = "${logCount.successCount}",
                                color = onSuccessContainerColor,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    if (logCount.failureCount > 0) {
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
                                text = "${logCount.failureCount}",
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