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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.core.content.IntentCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    onDeveloperInfoClick: () -> Unit
) {
    val darkThemeEnabled by settingsViewModel.darkThemeEnabled.collectAsStateWithLifecycle()
    val isUnusedAppRestrictionEnabled by settingsViewModel.isUnusedAppRestrictionEnabled.collectAsStateWithLifecycle()

    SettingsContent(
        darkThemeEnabled = { darkThemeEnabled },
        isUnusedAppRestrictionEnabled = { isUnusedAppRestrictionEnabled },
        onDarkThemeEnabled = settingsViewModel::setDarkThemeEnabled,
        onDeveloperInfoClick = onDeveloperInfoClick
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SettingsContent(
    darkThemeEnabled: () -> Boolean,
    isUnusedAppRestrictionEnabled: () -> Result<Boolean>,
    onDarkThemeEnabled: (Boolean) -> Unit,
    onDeveloperInfoClick: () -> Unit
) {
    val activity = LocalActivity.current
    val activityResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {}
    )
    val ignoreBatteryOptimizations =
        rememberSpecialPermissionState(
            specialPermission = SpecialPermission.IgnoreBatteryOptimization,
            onPermissionResult = {}
        )

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
                key = "themeHeader"
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
                key = "darkModeSwitch"
            ) {
                ListItem(
                    modifier = Modifier.toggleable(
                        value = darkThemeEnabled(),
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
                            checked = darkThemeEnabled(),
                            onCheckedChange = null
                        )
                    }
                )
            }

            item(
                key = "maintenance"
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
                key = "ignoreBatteryOptimization"
            ) {
                ListItem(
                    modifier = Modifier.toggleable(
                        value = ignoreBatteryOptimizations.status.isGranted,
                        role = Role.Switch,
                        onValueChange = {
                            ignoreBatteryOptimizations.launchPermissionRequest()
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
                            checked = ignoreBatteryOptimizations.status.isGranted,
                            onCheckedChange = null
                        )
                    }
                )
            }

            item(
                key = "unusedAppRestriction"
            ) {
                ListItem(
                    modifier = Modifier.toggleable(
                        value = !isUnusedAppRestrictionEnabled().getOrDefault(false),
                        role = Role.Switch,
                        onValueChange = {
                            activityResult.launch(
                                IntentCompat.createManageUnusedAppRestrictionsIntent(
                                    activity,
                                    activity.packageName
                                )
                            )
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
                            checked = !isUnusedAppRestrictionEnabled().getOrDefault(false),
                            onCheckedChange = null
                        )
                    }
                )
            }

            item(
                key = "othersHeader"
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
                key = "developerInfo"
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
                key = "openSourceLicences"
            ) {
                ListItem(
                    modifier = Modifier.clickable {
                        with(Intent(activity, OssLicensesMenuActivity::class.java)) {
                            if (resolveActivity(activity.packageManager) != null && !isDeviceNexus5X()) {
                                activity.startActivity(this)
                            }
                        }
                    },
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
