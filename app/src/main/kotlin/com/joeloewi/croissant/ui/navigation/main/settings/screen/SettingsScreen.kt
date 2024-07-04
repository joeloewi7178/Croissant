package com.joeloewi.croissant.ui.navigation.main.settings.screen

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.semantics.Role
import androidx.core.content.IntentCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.joeloewi.croissant.R
import com.joeloewi.croissant.ui.navigation.main.CroissantNavigation
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.SpecialPermission
import com.joeloewi.croissant.util.isDeviceNexus5X
import com.joeloewi.croissant.util.rememberSpecialPermissionState
import com.joeloewi.croissant.viewmodel.SettingsViewModel
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
            item(
                key = "themeHeader",
                contentType = "Header"
            ) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(id = R.string.theme),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }

            item(
                key = "darkModeSwitch",
                contentType = "ThemeContent"
            ) {
                ListItem(
                    modifier = Modifier.toggleable(
                        value = state.darkThemeEnabled,
                        role = Role.Switch,
                        onValueChange = onDarkThemeEnabled
                    ),
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = Icons.Default.DarkMode.name
                        )
                    },
                    headlineContent = {
                        Text(text = stringResource(id = R.string.dark_theme))
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(id = R.string.always_use_dark_theme)
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = state.darkThemeEnabled,
                            onCheckedChange = null
                        )
                    }
                )
            }

            item(
                key = "maintenanceHeader",
                contentType = "Header"
            ) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(id = R.string.maintenance),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }

            item(
                key = "ignoreBatteryOptimization",
                contentType = "MaintenanceContent"
            ) {
                ListItem(
                    modifier = Modifier.toggleable(
                        value = state.ignoreBatteryOptimizations,
                        role = Role.Switch,
                        onValueChange = {
                            onIgnoreBatteryOptimizationsValueChange()
                        }
                    ),
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.BatteryFull,
                            contentDescription = Icons.Default.BatteryFull.name
                        )
                    },
                    headlineContent = {
                        Text(text = stringResource(id = R.string.ignore_battery_optimizations))
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(id = R.string.ignore_battery_optimizations_description)
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = state.ignoreBatteryOptimizations,
                            onCheckedChange = null
                        )
                    }
                )
            }

            item(
                key = "unusedAppRestriction",
                contentType = "MaintenanceContent"
            ) {
                ListItem(
                    modifier = Modifier.toggleable(
                        enabled = state.canModifyUnusedAppRestriction,
                        value = !state.isUnusedAppRestrictionEnabled,
                        role = Role.Switch,
                        onValueChange = {
                            onIsUnusedAppRestrictionEnabledValueChange()
                        }
                    ),
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.AppRegistration,
                            contentDescription = Icons.Default.AppRegistration.name
                        )
                    },
                    headlineContent = {
                        Text(text = stringResource(id = R.string.disable_app_hibernation))
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(id = R.string.disable_app_hibernation_description)
                        )
                    },
                    trailingContent = {
                        Switch(
                            enabled = state.canModifyUnusedAppRestriction,
                            checked = !state.isUnusedAppRestrictionEnabled,
                            onCheckedChange = null
                        )
                    }
                )
            }

            item(
                key = "othersHeader",
                contentType = "Header"
            ) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(id = R.string.others),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }

            item(
                key = "developerInfo",
                contentType = "OthersContent"
            ) {
                ListItem(
                    modifier = Modifier.clickable {
                        onDeveloperInfoClick()
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = Icons.Default.Person.name
                        )
                    },
                    headlineContent = {
                        Text(text = stringResource(id = R.string.developer_info))
                    }
                )
            }

            item(
                key = "openSourceLicences",
                contentType = "OthersContent"
            ) {
                ListItem(
                    modifier = Modifier.clickable(onClick = onClickViewOpenSourceLicenses),
                    leadingContent = {
                        Icon(
                            painter = rememberVectorPainter(
                                image = ImageVector.vectorResource(
                                    id = R.drawable.ic_baseline_open_source_24
                                )
                            ),
                            contentDescription = null
                        )
                    },
                    headlineContent = {
                        Text(text = stringResource(id = R.string.open_source_licenses))
                    }
                )
            }
        }
    }
}
