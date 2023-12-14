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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.joeloewi.croissant.R
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.DoubleDp
import com.joeloewi.croissant.ui.theme.HalfDp
import com.joeloewi.croissant.ui.theme.onSuccessContainerColor
import com.joeloewi.croissant.ui.theme.successContainerColor
import com.joeloewi.croissant.ui.theme.warningContainerColor
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.generateCalendarDays
import com.joeloewi.croissant.util.isCompactWindowSize
import com.joeloewi.croissant.util.navigationIconButton
import com.joeloewi.croissant.viewmodel.AttendanceLogsCalendarViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.format.DateTimeFormatter

@Composable
fun AttendanceLogsCalendarScreen(
    attendanceLogsCalendarViewModel: AttendanceLogsCalendarViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    val deleteAllState by attendanceLogsCalendarViewModel.deleteAllState.collectAsStateWithLifecycle()
    val year by attendanceLogsCalendarViewModel.year.collectAsStateWithLifecycle()

    AttendanceLogsCalendarContent(
        deleteAllState = { deleteAllState },
        year = { year },
        onYearChange = attendanceLogsCalendarViewModel::setYear,
        onDeleteAll = attendanceLogsCalendarViewModel::deleteAll,
        onNavigateUp = onNavigateUp
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun AttendanceLogsCalendarContent(
    deleteAllState: () -> Lce<Int>,
    year: () -> Year,
    onYearChange: (Year) -> Unit,
    onDeleteAll: () -> Unit,
    onNavigateUp: () -> Unit
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState { Month.entries.size }
    val snackbarHostState = remember { SnackbarHostState() }
    val (expanded, onExpandedChange) = rememberSaveable { mutableStateOf(false) }
    val years by remember(Year.now()) {
        derivedStateOf {
            (1900..Year.now().value).reversed().map { Year.of(it) }
        }
    }
    val viewModelStoreOwner = LocalViewModelStoreOwner.current
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            snapshotFlow(deleteAllState).catch { }.filterIsInstance<Lce.Content<Int>>().collect {
                val rowCount = it.content

                if (rowCount != -1) {
                    snackbarHostState.showSnackbar(
                        context.getString(
                            R.string.logs_deleted,
                            rowCount
                        )
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.execution_log))
                },
                navigationIcon = viewModelStoreOwner.navigationIconButton(
                    onClick = onNavigateUp
                ),
                actions = {
                    IconButton(
                        onClick = {
                            showDeleteConfirmationDialog = true
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
                            text = year().value.toString(),
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
                                        onYearChange(year)
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
                        year().atMonth(it + 1).format(DateTimeFormatter.ofPattern("yyyy-MM"))
                    }
                ) { page ->

                    MonthPage(
                        page = page,
                        year = year,
                        dayLogCount = { _, _, _ -> emptyFlow() },
                        onClickDay = { }
                    )
                }
            }

            if (showDeleteConfirmationDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showDeleteConfirmationDialog = false
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
                                showDeleteConfirmationDialog = false
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

@Composable
private fun MonthPage(
    page: Int,
    year: () -> Year,
    dayLogCount: (Year, Month, Int) -> Flow<Pair<Long, Long>>,
    onClickDay: (localDate: String) -> Unit
) {
    val updatedOnDayClick by rememberUpdatedState(newValue = onClickDay)
    val month = remember(page) { Month.entries[page] }
    val totalDays =
        remember(year().atMonth(month)) {
            year().atMonth(month).generateCalendarDays()
        }

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

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
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
                    .clip(MaterialTheme.shapes.extraSmall)
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