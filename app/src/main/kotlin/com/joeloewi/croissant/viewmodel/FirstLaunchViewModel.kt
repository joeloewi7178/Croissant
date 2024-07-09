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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.joeloewi.croissant.domain.usecase.SettingsUseCase
import com.joeloewi.croissant.util.CroissantPermission
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class FirstLaunchViewModel @Inject constructor(
    private val setIsFirstLaunchSettingsUseCase: SettingsUseCase.SetIsFirstLaunch,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ContainerHost<FirstLaunchViewModel.State, FirstLaunchViewModel.SideEffect> {

    override val container: Container<State, SideEffect> = container(State())

    fun onPermissionGranted(vararg croissantPermission: CroissantPermission) = intent {
        reduce {
            state.copy(
                grantedPermissions = state.grantedPermissions.toMutableList().apply {
                    addAll(croissantPermission)
                }.toImmutableList()
            )
        }
    }

    fun onLaunchMultiplePermissionRequest() = intent {
        postSideEffect(SideEffect.LaunchMultiplePermissionsRequest)
    }

    fun onLaunchScheduleExactAlarmPermissionRequest() = intent {
        postSideEffect(SideEffect.LaunchScheduleExactAlarmPermissionRequest)
    }

    fun onNavigateToAttendances() = intent {
        setIsFirstLaunchSettingsUseCase(false)
        postSideEffect(SideEffect.NavigateToAttendances)
    }

    data class State(
        val croissantPermissions: ImmutableList<CroissantPermission> = CroissantPermission.entries.toImmutableList(),
        val normalPermissions: ImmutableList<String> = persistentListOf(
            CroissantPermission.AccessHoYoLABSession.permission,
            CroissantPermission.PostNotifications.permission
        ),
        val grantedPermissions: ImmutableList<CroissantPermission> = persistentListOf()
    )

    sealed class SideEffect {
        data object LaunchMultiplePermissionsRequest : SideEffect()
        data object LaunchScheduleExactAlarmPermissionRequest : SideEffect()
        data object NavigateToAttendances : SideEffect()
    }
}