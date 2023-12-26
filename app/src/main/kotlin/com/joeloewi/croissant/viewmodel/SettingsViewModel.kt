package com.joeloewi.croissant.viewmodel

import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.domain.usecase.SettingsUseCase
import com.joeloewi.croissant.domain.usecase.SystemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getSettingsUseCase: SettingsUseCase.GetSettings,
    private val setDarkThemeEnabledSettingUseCase: SettingsUseCase.SetDarkThemeEnabled,
    private val isUnusedAppRestrictionEnabledUseCase: SystemUseCase.IsUnusedAppRestrictionEnabled
) : ViewModel() {
    val darkThemeEnabled =
        getSettingsUseCase().map { it.darkThemeEnabled }.flowOn(Dispatchers.IO).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )
    val isUnusedAppRestrictionEnabled = flow {
        emit(isUnusedAppRestrictionEnabledUseCase())
    }.stateIn(
        scope = ProcessLifecycleOwner.get().lifecycleScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Result.success(false)
    )

    fun setDarkThemeEnabled(darkThemeEnabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            setDarkThemeEnabledSettingUseCase(darkThemeEnabled)
        }
    }
}