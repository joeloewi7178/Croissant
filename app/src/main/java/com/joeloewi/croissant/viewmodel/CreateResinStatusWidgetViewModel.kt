package com.joeloewi.croissant.viewmodel

import android.app.Application
import android.appwidget.AppWidgetManager
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.ResinStatusWidgetConfigurationDestination
import com.joeloewi.croissant.worker.RefreshResinStatusWorker
import com.joeloewi.croissant.domain.entity.Account
import com.joeloewi.croissant.domain.entity.ResinStatusWidget
import com.joeloewi.croissant.domain.entity.UserInfo
import com.joeloewi.croissant.domain.usecase.AccountUseCase
import com.joeloewi.croissant.domain.usecase.HoYoLABUseCase
import com.joeloewi.croissant.domain.usecase.ResinStatusWidgetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class CreateResinStatusWidgetViewModel @Inject constructor(
    private val application: Application,
    private val getUserFullInfoHoYoLABUseCase: HoYoLABUseCase.GetUserFullInfo,
    private val insertResinStatusWidgetUseCase: ResinStatusWidgetUseCase.Insert,
    private val insertAccountUseCase: AccountUseCase.Insert,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _appWidgetIdKey =
        ResinStatusWidgetConfigurationDestination.CreateResinStatusWidgetScreen.APP_WIDGET_ID
    val selectableIntervals = listOf(15L, 30L)

    private val _createResinStatusWidgetState = MutableStateFlow<Lce<List<Long>>>(
        Lce.Content(
            listOf()
        )
    )
    private val _getUserInfoState = MutableStateFlow<Lce<UserInfo?>>(Lce.Content(null))
    private val _interval = MutableStateFlow(selectableIntervals.first())

    val appWidgetId =
        savedStateHandle.get<Int>(_appWidgetIdKey) ?: AppWidgetManager.INVALID_APPWIDGET_ID
    val createResinStatusWidgetState = _createResinStatusWidgetState.asStateFlow()
    val getUserInfoState = _getUserInfoState.asStateFlow()
    val interval = _interval.asStateFlow()
    val userInfos = SnapshotStateList<Pair<String, UserInfo>>()

    fun onReceiveCookie(cookie: String) {
        _getUserInfoState.update { Lce.Loading }
        viewModelScope.launch(Dispatchers.IO) {
            _getUserInfoState.update {
                getUserFullInfoHoYoLABUseCase(cookie).mapCatching {
                    it.data?.userInfo!!
                }.fold(
                    onSuccess = {
                        withContext(Dispatchers.Main) {
                            userInfos.add(cookie to it)
                        }
                        Lce.Content(it)
                    },
                    onFailure = {
                        Lce.Error(it)
                    }
                )
            }
        }
    }

    fun setInterval(interval: Long) {
        _interval.update { interval }
    }

    fun configureAppWidget() {
        _createResinStatusWidgetState.update { Lce.Loading }
        viewModelScope.launch(Dispatchers.IO) {
            _createResinStatusWidgetState.update {
                this.runCatching {
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

                    val periodicWorkRequest = PeriodicWorkRequest.Builder(
                        RefreshResinStatusWorker::class.java,
                        _interval.value,
                        TimeUnit.MINUTES
                    ).setInputData(
                        workDataOf(RefreshResinStatusWorker.APP_WIDGET_ID to appWidgetId)
                    ).setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    ).build()

                    WorkManager.getInstance(application)
                        .enqueueUniquePeriodicWork(
                            resinStatusWidget.refreshGenshinResinStatusWorkerName.toString(),
                            ExistingPeriodicWorkPolicy.REPLACE,
                            periodicWorkRequest
                        ).await()

                    insertAccountUseCase(*accounts.toTypedArray())
                }.fold(
                    onSuccess = {
                        Lce.Content(it)
                    },
                    onFailure = {
                        Lce.Error(it)
                    }
                )
            }
        }
    }
}