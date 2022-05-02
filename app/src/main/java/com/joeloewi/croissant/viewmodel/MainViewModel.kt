package com.joeloewi.croissant.viewmodel

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.data.proto.SettingsSerializer
import com.joeloewi.croissant.data.proto.settingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
) : ViewModel() {
    private val _settings = application.settingsDataStore.data.flowOn(Dispatchers.IO).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = SettingsSerializer.defaultValue
    )

    val isFirstLaunch = _settings.map { it.isFirstLaunch }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = SettingsSerializer.defaultValue.isFirstLaunch
    )

    init {
        _settings.map { it.darkThemeEnabled }.distinctUntilChanged()
            .onEach { darkThemeEnabled ->
                if (darkThemeEnabled) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }.launchIn(viewModelScope)
    }

    fun setIsFirstLaunch(isFirstLaunch: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            application.settingsDataStore.updateData {
                it.toBuilder().setIsFirstLaunch(isFirstLaunch).build()
            }
        }
    }
}