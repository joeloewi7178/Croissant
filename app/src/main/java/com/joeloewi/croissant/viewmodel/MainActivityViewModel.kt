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
import com.joeloewi.croissant.util.HourFormat
import com.joeloewi.croissant.util.is24HourFormat
import com.joeloewi.domain.usecase.SettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
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