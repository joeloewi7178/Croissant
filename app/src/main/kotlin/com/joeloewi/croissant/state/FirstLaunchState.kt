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

package com.joeloewi.croissant.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import com.joeloewi.croissant.viewmodel.FirstLaunchViewModel

@Stable
class FirstLaunchState(
    private val navController: NavHostController,
    private val firstLaunchViewModel: FirstLaunchViewModel
) {
    fun onFirstLaunchChange(isFirstLaunch: Boolean) {
        firstLaunchViewModel.setIsFirstLaunch(isFirstLaunch)
    }

    fun navigateToAttendancesScreen() {
        navController.navigate(AttendancesDestination.AttendancesScreen.route) {
            navController.currentDestination?.let {
                popUpTo(it.id) {
                    inclusive = true
                }
            }
        }
    }
}

@Composable
fun rememberFirstLaunchState(
    navController: NavHostController,
    firstLaunchViewModel: FirstLaunchViewModel
) = remember(
    navController,
    firstLaunchViewModel
) {
    FirstLaunchState(
        navController = navController,
        firstLaunchViewModel = firstLaunchViewModel
    )
}