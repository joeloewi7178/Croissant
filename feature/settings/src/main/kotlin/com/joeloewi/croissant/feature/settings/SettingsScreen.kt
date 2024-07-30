package com.joeloewi.croissant.feature.settings

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.core.content.IntentCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.joeloewi.croissant.core.ui.CroissantNavigation
import com.joeloewi.croissant.core.ui.LocalActivity
import com.joeloewi.croissant.core.ui.maintenanceContentDisableAppHibernation
import com.joeloewi.croissant.core.ui.maintenanceContentIgnoreBatteryOptimization
import com.joeloewi.croissant.core.ui.maintenanceHeader
import com.joeloewi.croissant.core.ui.othersContentDeveloperInfo
import com.joeloewi.croissant.core.ui.othersContentOpenSourceLicences
import com.joeloewi.croissant.core.ui.othersHeader
import com.joeloewi.croissant.core.ui.themeContentDarkModeSwitch
import com.joeloewi.croissant.core.ui.themeHeader
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    onDeveloperInfoClick: () -> Unit
) {
    val state by settingsViewModel.collectAsState()
    val activity = LocalActivity.current
    val activityResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { settingsViewModel.onIsUnusedAppRestrictionEnabledPermissionChanged() }
    )
    val ignoreBatteryOptimizationsPermissionState = rememberSpecialPermissionState(
        specialPermission = SpecialPermission.IgnoreBatteryOptimization,
        onPermissionResult = {}
    )

    LaunchedEffect(ignoreBatteryOptimizationsPermissionState) {
        snapshotFlow { ignoreBatteryOptimizationsPermissionState.status.isGranted }.collect {
            settingsViewModel.onIgnoreBatteryOptimizationPermissionChanged(it)
        }
    }

    settingsViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            SettingsViewModel.SideEffect.LaunchIgnoreBatteryOptimizationPermissionRequest -> {
                ignoreBatteryOptimizationsPermissionState.launchPermissionRequest()
            }

            SettingsViewModel.SideEffect.LaunchManageUnusedAppRestrictionIntent -> {
                runCatching {
                    activityResult.launch(
                        IntentCompat.createManageUnusedAppRestrictionsIntent(
                            activity,
                            activity.packageName
                        )
                    )
                }
            }

            SettingsViewModel.SideEffect.LaunchOpenSourceLicensesIntent -> {
                with(Intent(activity, OssLicensesMenuActivity::class.java)) {
                    if (resolveActivity(activity.packageManager) != null && !isDeviceNexus5X()) {
                        activity.startActivity(this)
                    }
                }
            }
        }
    }

    SettingsContent(
        state = state,
        onDarkThemeEnabled = settingsViewModel::setDarkThemeEnabled,
        onDeveloperInfoClick = onDeveloperInfoClick,
        onIgnoreBatteryOptimizationsValueChange = settingsViewModel::onIgnoreBatteryOptimizationsValueChange,
        onIsUnusedAppRestrictionEnabledValueChange = settingsViewModel::onIsUnusedAppRestrictionEnabledValueChange,
        onClickViewOpenSourceLicenses = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    state: SettingsViewModel.State,
    onDarkThemeEnabled: (Boolean) -> Unit,
    onDeveloperInfoClick: () -> Unit,
    onIgnoreBatteryOptimizationsValueChange: () -> Unit,
    onIsUnusedAppRestrictionEnabledValueChange: () -> Unit,
    onClickViewOpenSourceLicenses: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = CroissantNavigation.Settings.resourceId))
                }
            )
        },
        contentWindowInsets = WindowInsets.systemBars.exclude(WindowInsets.navigationBars)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            themeHeader(headerText = stringResource(id = R.string.theme))

            themeContentDarkModeSwitch(
                darkThemeEnabled = state.darkThemeEnabled,
                darkThemeText = stringResource(id = R.string.dark_theme),
                alwaysUseDarkThemeText = stringResource(id = R.string.always_use_dark_theme),
                onDarkThemeEnabled = onDarkThemeEnabled
            )

            maintenanceHeader(
                maintenanceText = stringResource(id = R.string.maintenance)
            )

            maintenanceContentIgnoreBatteryOptimization(
                ignoreBatteryOptimizations = state.ignoreBatteryOptimizations,
                ignoreBatteryOptimizationsText = stringResource(id = R.string.ignore_battery_optimizations),
                ignoreBatteryOptimizationsDescriptionText = stringResource(id = R.string.ignore_battery_optimizations_description),
                onIgnoreBatteryOptimizationsValueChange = onIgnoreBatteryOptimizationsValueChange
            )

            maintenanceContentDisableAppHibernation(
                canModifyUnusedAppRestriction = state.canModifyUnusedAppRestriction,
                isUnusedAppRestrictionEnabled = state.isUnusedAppRestrictionEnabled,
                disableAppHibernationText = stringResource(id = R.string.disable_app_hibernation),
                disableAppHibernationDescriptionText = stringResource(id = R.string.disable_app_hibernation_description),
                onIsUnusedAppRestrictionEnabledValueChange = onIsUnusedAppRestrictionEnabledValueChange
            )

            othersHeader(othersText = stringResource(id = R.string.others))

            othersContentDeveloperInfo(
                developerInfoText = stringResource(id = com.joeloewi.croissant.core.ui.R.string.developer_info),
                onDeveloperInfoClick = onDeveloperInfoClick
            )

            othersContentOpenSourceLicences(
                icon = rememberVectorPainter(
                    image = ImageVector.vectorResource(
                        id = com.joeloewi.croissant.core.ui.R.drawable.ic_baseline_open_source_24
                    )
                ),
                openSourceLicensesText = stringResource(id = com.joeloewi.croissant.core.ui.R.string.open_source_licenses),
                onClickViewOpenSourceLicenses = onClickViewOpenSourceLicenses
            )
        }
    }
}
