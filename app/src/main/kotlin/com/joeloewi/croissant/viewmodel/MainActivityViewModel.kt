package com.joeloewi.croissant.viewmodel

import android.app.Application
import android.os.Build
import android.text.format.DateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.requestUpdateFlow
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.joeloewi.croissant.domain.usecase.SettingsUseCase
import com.joeloewi.croissant.util.HourFormat
import com.joeloewi.croissant.util.is24HourFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val application: Application,
    getSettingsUseCase: SettingsUseCase.GetSettings,
) : ViewModel() {
    private val _settings = getSettingsUseCase()

    val hourFormat = application.is24HourFormat.flowOn(Dispatchers.Default).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = HourFormat.fromSystemHourFormat(DateFormat.is24HourFormat(application))
    )
    val appUpdateResultState =
        flow {
            emit(Build.MODEL)
        }.filter {
            !listOf("LG-H790", "LG-H791").contains(it.uppercase())
        }.map {
            AppUpdateManagerFactory.create(application)
        }.flatMapConcat {
            it.requestUpdateFlow()
        }.flowOn(Dispatchers.Default).catch { cause ->
            FirebaseCrashlytics.getInstance().apply {
                log("AppUpdateManager")
                recordException(cause)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = AppUpdateResult.NotAvailable
        )
    val darkThemeEnabled = _settings.map { it.darkThemeEnabled }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = false
    )
}