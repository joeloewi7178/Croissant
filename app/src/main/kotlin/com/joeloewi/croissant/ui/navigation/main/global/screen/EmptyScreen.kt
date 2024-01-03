package com.joeloewi.croissant.ui.navigation.main.global.screen

import android.app.AlarmManager
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joeloewi.croissant.util.CroissantPermission
import com.joeloewi.croissant.util.canScheduleExactAlarmsCompat
import com.joeloewi.croissant.viewmodel.EmptyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.withContext

@Composable
fun EmptyScreen(
    emptyViewModel: EmptyViewModel = hiltViewModel(),
    onShowFirstLaunchScreen: () -> Unit,
    onShowDefaultScreen: () -> Unit,
) {
    val isFirstLaunch by emptyViewModel.isFirstLaunch.collectAsStateWithLifecycle()

    EmptyContent(
        isFirstLaunch = { isFirstLaunch },
        onShowFirstLaunchScreen = onShowFirstLaunchScreen,
        onShowDefaultScreen = onShowDefaultScreen
    )
}

@Composable
private fun EmptyContent(
    isFirstLaunch: () -> Boolean?,
    onShowFirstLaunchScreen: () -> Unit,
    onShowDefaultScreen: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            snapshotFlow { isFirstLaunch() }.catch { }.filterNotNull()
                .collect { showFirstLaunchScreen ->
                    withContext(Dispatchers.Main) {
                        val anyOfPermissionsIsDenied = listOf(
                            CroissantPermission.AccessHoYoLABSession.permission,
                            CroissantPermission.PostNotifications.permission
                        ).any {
                            ContextCompat.checkSelfPermission(
                                context,
                                it
                            ) == PackageManager.PERMISSION_DENIED
                        } || context.getSystemService<AlarmManager>()
                            ?.canScheduleExactAlarmsCompat() == false

                        if (showFirstLaunchScreen || anyOfPermissionsIsDenied) {
                            onShowFirstLaunchScreen()
                        } else {
                            onShowDefaultScreen()
                        }
                    }
                }
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {

        }
    }
}