/*
 *    Copyright 2022 joeloewi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.joeloewi.croissant.viewmodel

import android.appwidget.AppWidgetManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.domain.SettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class WidgetConfigurationActivityViewModel @Inject constructor(
    getSettingsUseCase: SettingsUseCase.GetSettings,
    savedStateHandle: SavedStateHandle
) : ViewModel(), ContainerHost<WidgetConfigurationActivityViewModel.State, Nothing> {
    private val _darkThemeEnabled = getSettingsUseCase().map { it.darkThemeEnabled }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = false
    )
    private val _appWidgetId = savedStateHandle.getStateFlow(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
    )

    override val container: Container<State, Nothing> = container(State()) {
        intent { _darkThemeEnabled.collect { reduce { state.copy(isDarkThemEnabled = it) } } }
        intent { _appWidgetId.collect { reduce { state.copy(appWidgetId = it) } } }
    }

    data class State(
        val isDarkThemEnabled: Boolean = false,
        val appWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID
    )
}