package com.joeloewi.croissant.viewmodel

import androidx.lifecycle.ViewModel
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
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getSettingsUseCase: SettingsUseCase.GetSettings,
    private val setDarkThemeEnabledSettingUseCase: SettingsUseCase.SetDarkThemeEnabled,
    private val isUnusedAppRestrictionEnabledUseCase: SystemUseCase.IsUnusedAppRestrictionEnabled
) : ViewModel(), ContainerHost<SettingsViewModel.State, SettingsViewModel.SideEffect> {
    private val _darkThemeEnabled =
        getSettingsUseCase().map { it.darkThemeEnabled }.flowOn(Dispatchers.IO).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )
    private val _isUnusedAppRestrictionEnabled = flow {
        emit(isUnusedAppRestrictionEnabledUseCase())
    }.flowOn(Dispatchers.IO).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Result.success(false)
    )

    override val container: Container<State, SideEffect> = container(State()) {
        intent {
            _darkThemeEnabled.collect {
                reduce { state.copy(darkThemeEnabled = it) }
            }
        }

        intent {
            _isUnusedAppRestrictionEnabled.collect { result ->
                result.onSuccess {
                    reduce {
                        state.copy(
                            canModifyUnusedAppRestriction = true,
                            isUnusedAppRestrictionEnabled = it
                        )
                    }
                }.onFailure {
                    reduce {
                        state.copy(
                            canModifyUnusedAppRestriction = false,
                        )
                    }
                }
            }
        }
    }

    fun onIsUnusedAppRestrictionEnabledPermissionChanged() = intent {
        isUnusedAppRestrictionEnabledUseCase().onSuccess {
            reduce { state.copy(isUnusedAppRestrictionEnabled = it) }
        }
    }

    fun onIgnoreBatteryOptimizationPermissionChanged(isGranted: Boolean) = intent {
        reduce { state.copy(ignoreBatteryOptimizations = isGranted) }
    }

    fun setDarkThemeEnabled(darkThemeEnabled: Boolean) = intent {
        setDarkThemeEnabledSettingUseCase(darkThemeEnabled)
    }

    fun onIgnoreBatteryOptimizationsValueChange() = intent {
        postSideEffect(SideEffect.LaunchIgnoreBatteryOptimizationPermissionRequest)
    }

    fun onIsUnusedAppRestrictionEnabledValueChange() = intent {
        postSideEffect(SideEffect.LaunchManageUnusedAppRestrictionIntent)
    }

    fun onClickViewOpenSourceLicenses() = intent {
        postSideEffect(SideEffect.LaunchOpenSourceLicensesIntent)
    }

    data class State(
        val darkThemeEnabled: Boolean = false,
        val canModifyUnusedAppRestriction: Boolean = false,
        val isUnusedAppRestrictionEnabled: Boolean = false,
        val ignoreBatteryOptimizations: Boolean = false
    )

    sealed class SideEffect {
        data object LaunchIgnoreBatteryOptimizationPermissionRequest : SideEffect()
        data object LaunchManageUnusedAppRestrictionIntent : SideEffect()
        data object LaunchOpenSourceLicensesIntent : SideEffect()
    }
}