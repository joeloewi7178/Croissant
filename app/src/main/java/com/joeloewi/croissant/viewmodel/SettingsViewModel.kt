package com.joeloewi.croissant.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.Settings
import com.joeloewi.croissant.data.proto.settingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {
    val settings = application.settingsDataStore.data
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Settings.getDefaultInstance()
        )

    fun setDarkThemeEnabled(darkThemeEnabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            application.settingsDataStore.updateData { settings ->
                settings.toBuilder()
                    .setDarkThemeEnabled(darkThemeEnabled)
                    .build()
            }
        }
    }
}