package com.joeloewi.croissant.viewmodel

import android.appwidget.AppWidgetManager
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import androidx.work.await
import com.joeloewi.croissant.domain.entity.Account
import com.joeloewi.croissant.domain.entity.ResinStatusWidget
import com.joeloewi.croissant.domain.entity.UserInfo
import com.joeloewi.croissant.domain.usecase.AccountUseCase
import com.joeloewi.croissant.domain.usecase.HoYoLABUseCase
import com.joeloewi.croissant.domain.usecase.ResinStatusWidgetUseCase
import com.joeloewi.croissant.state.ILCE
import com.joeloewi.croissant.state.foldAsILCE
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.ResinStatusWidgetConfigurationDestination
import com.joeloewi.croissant.worker.RefreshResinStatusWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class CreateResinStatusWidgetViewModel @Inject constructor(
    private val workManager: WorkManager,
    private val getUserFullInfoHoYoLABUseCase: HoYoLABUseCase.GetUserFullInfo,
    private val insertResinStatusWidgetUseCase: ResinStatusWidgetUseCase.Insert,
    private val insertAccountUseCase: AccountUseCase.Insert,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _appWidgetIdKey =
        ResinStatusWidgetConfigurationDestination.CreateResinStatusWidgetScreen.APP_WIDGET_ID
    val selectableIntervals = listOf(15L, 30L)

    private val _createResinStatusWidgetState = MutableStateFlow<ILCE<List<Long>>>(ILCE.Idle)
    private val _getUserInfoState = MutableStateFlow<ILCE<UserInfo>>(ILCE.Idle)
    private val _interval = MutableStateFlow(selectableIntervals.first())

    val appWidgetId =
        savedStateHandle.getStateFlow(_appWidgetIdKey, AppWidgetManager.INVALID_APPWIDGET_ID)
    val createResinStatusWidgetState = _createResinStatusWidgetState.asStateFlow()
    val getUserInfoState = _getUserInfoState.asStateFlow()
    val interval = _interval.asStateFlow()
    val userInfos = SnapshotStateList<Pair<String, UserInfo>>()

    fun onReceiveCookie(cookie: String) {
        _getUserInfoState.value = ILCE.Loading
        viewModelScope.launch(Dispatchers.IO) {
            _getUserInfoState.value = getUserFullInfoHoYoLABUseCase(cookie).mapCatching {
                it.data?.userInfo!!
            }.mapCatching {
                withContext(Dispatchers.Main) {
                    userInfos.add(cookie to it)
                }
                it
            }.foldAsILCE()
        }
    }

    fun setInterval(interval: Long) {
        _interval.value = interval
    }

    fun configureAppWidget() {
        _createResinStatusWidgetState.value = ILCE.Loading
        viewModelScope.launch(Dispatchers.IO) {
            _createResinStatusWidgetState.value = runCatching {
                val appWidgetId = appWidgetId.value

                val resinStatusWidget = ResinStatusWidget(
                    appWidgetId = appWidgetId,
                    interval = _interval.value,
                )

                val resinStatusWidgetId = insertResinStatusWidgetUseCase(
                    resinStatusWidget = resinStatusWidget
                )

                val accounts = userInfos
                    .map {
                        Account(
                            resinStatusWidgetId = resinStatusWidgetId,
                            cookie = it.first,
                            uid = it.second.uid
                        )
                    }

                val periodicWorkRequest = RefreshResinStatusWorker.buildPeriodicWork(
                    repeatInterval = _interval.value,
                    repeatIntervalTimeUnit = TimeUnit.MINUTES,
                    appWidgetId = appWidgetId
                )

                workManager.enqueueUniquePeriodicWork(
                    resinStatusWidget.refreshGenshinResinStatusWorkerName.toString(),
                    ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                    periodicWorkRequest
                ).await()

                insertAccountUseCase(*accounts.toTypedArray())
            }.foldAsILCE()
        }
    }
}