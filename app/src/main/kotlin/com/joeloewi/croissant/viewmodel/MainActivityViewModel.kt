package com.joeloewi.croissant.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.requestUpdateFlow
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.joeloewi.croissant.domain.usecase.SettingsUseCase
import com.joeloewi.croissant.domain.usecase.SystemUseCase
import com.joeloewi.croissant.util.HourFormat
import com.joeloewi.croissant.util.isDeviceNexus5X
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
    private val appUpdateManager: AppUpdateManager,
    is24HourFormatImmediate: Boolean,
    getSettingsUseCase: SettingsUseCase.GetSettings,
    is24HourFormat: SystemUseCase.Is24HourFormat,
    isDeviceRooted: SystemUseCase.IsDeviceRooted
) : ViewModel() {
    private val _settings = getSettingsUseCase()

    val hourFormat = is24HourFormat().map {
        HourFormat.fromSystemHourFormat(it)
    }.catch { }.flowOn(Dispatchers.IO).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = HourFormat.fromSystemHourFormat(is24HourFormatImmediate)
    )
    val appUpdateResultState =
        flow {
            emit(Build.MODEL)
        }.filter {
            !isDeviceNexus5X()
        }.flatMapConcat {
            appUpdateManager.requestUpdateFlow()
        }.catch { cause ->
            Firebase.crashlytics.apply {
                log("AppUpdateManager")
                recordException(cause)
            }
        }.flowOn(Dispatchers.IO).stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = AppUpdateResult.NotAvailable
        )
    val darkThemeEnabled = _settings.map { it.darkThemeEnabled }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = false
    )
    val isDeviceRooted = flow {
        emit(isDeviceRooted())
    }.catch { }.flowOn(Dispatchers.IO).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = false
    )
}