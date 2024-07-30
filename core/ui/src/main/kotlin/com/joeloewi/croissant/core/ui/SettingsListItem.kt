package com.joeloewi.croissant.core.ui

import androidx.annotation.IdRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role

/*
 *    Copyright 2024. joeloewi
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
fun LazyListScope.themeHeader(
    headerText: String
) = item(
    key = "themeHeader",
    contentType = "Header"
) {
    ListItem(
        headlineContent = {
            Text(
                text = headerText,
                color = MaterialTheme.colorScheme.primary
            )
        }
    )
}

fun LazyListScope.themeContentDarkModeSwitch(
    darkThemeEnabled: Boolean,
    darkThemeText: String,
    alwaysUseDarkThemeText: String,
    onDarkThemeEnabled: (Boolean) -> Unit
) = item(
    key = "darkModeSwitch",
    contentType = "ThemeContent"
) {
    ListItem(
        modifier = Modifier.toggleable(
            value = darkThemeEnabled,
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
            Text(text = darkThemeText)
        },
        supportingContent = {
            Text(text = alwaysUseDarkThemeText)
        },
        trailingContent = {
            Switch(
                checked = darkThemeEnabled,
                onCheckedChange = null
            )
        }
    )
}

fun LazyListScope.maintenanceHeader(
    maintenanceText: String
) = item(
    key = "maintenanceHeader",
    contentType = "Header"
) {
    ListItem(
        headlineContent = {
            Text(
                text = maintenanceText,
                color = MaterialTheme.colorScheme.primary
            )
        }
    )
}

fun LazyListScope.maintenanceContentIgnoreBatteryOptimization(
    ignoreBatteryOptimizations: Boolean,
    ignoreBatteryOptimizationsText: String,
    ignoreBatteryOptimizationsDescriptionText: String,
    onIgnoreBatteryOptimizationsValueChange: () -> Unit
) = item(
    key = "ignoreBatteryOptimization",
    contentType = "MaintenanceContent"
) {
    ListItem(
        modifier = Modifier.toggleable(
            value = ignoreBatteryOptimizations,
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
            Text(text = ignoreBatteryOptimizationsText)
        },
        supportingContent = {
            Text(text = ignoreBatteryOptimizationsDescriptionText)
        },
        trailingContent = {
            Switch(
                checked = ignoreBatteryOptimizations,
                onCheckedChange = null
            )
        }
    )
}

fun LazyListScope.maintenanceContentDisableAppHibernation(
    canModifyUnusedAppRestriction: Boolean,
    isUnusedAppRestrictionEnabled: Boolean,
    disableAppHibernationText: String,
    disableAppHibernationDescriptionText: String,
    onIsUnusedAppRestrictionEnabledValueChange: () -> Unit
) = item(
    key = "disableAppHibernation",
    contentType = "MaintenanceContent"
) {
    ListItem(
        modifier = Modifier.toggleable(
            enabled = canModifyUnusedAppRestriction,
            value = !isUnusedAppRestrictionEnabled,
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
            Text(text = disableAppHibernationText)
        },
        supportingContent = {
            Text(text = disableAppHibernationDescriptionText)
        },
        trailingContent = {
            Switch(
                enabled = canModifyUnusedAppRestriction,
                checked = !isUnusedAppRestrictionEnabled,
                onCheckedChange = null
            )
        }
    )
}

fun LazyListScope.othersHeader(
    othersText: String
) = item(
    key = "othersHeader",
    contentType = "Header"
) {
    ListItem(
        headlineContent = {
            Text(
                text = othersText,
                color = MaterialTheme.colorScheme.primary
            )
        }
    )
}

fun LazyListScope.othersContentDeveloperInfo(
    developerInfoText: String,
    onDeveloperInfoClick: () -> Unit
) = item(
    key = "developerInfo",
    contentType = "OthersContent"
) {
    ListItem(
        modifier = Modifier.clickable(
            onClick = onDeveloperInfoClick
        ),
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = Icons.Default.Person.name
            )
        },
        headlineContent = {
            Text(text = developerInfoText)
        }
    )
}

fun LazyListScope.othersContentOpenSourceLicences(
    icon: Painter,
    openSourceLicensesText: String,
    onClickViewOpenSourceLicenses: () -> Unit
) = item(
    key = "openSourceLicences",
    contentType = "OthersContent"
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClickViewOpenSourceLicenses),
        leadingContent = {
            Icon(
                painter = icon ,
                contentDescription = null
            )
        },
        headlineContent = {
            Text(text = openSourceLicensesText )
        }
    )
}