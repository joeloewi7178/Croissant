package com.joeloewi.croissant.ui.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.joeloewi.croissant.Settings
import com.joeloewi.croissant.data.proto.settingsDataStore

@Composable
fun navigationIconButton(
    previousBackStackEntry: NavBackStackEntry?,
    onClick: () -> Unit
): @Composable () -> Unit =
    if (previousBackStackEntry != null) {
        {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = Icons.Default.ArrowBack.name
                )
            }
        }
    } else {
        {

        }
    }

fun <T> getResultFromPreviousComposable(
    navController: NavController,
    key: String
): T? = navController.currentBackStackEntry?.savedStateHandle?.run {
    get<T>(key).apply {
        this@run.remove<T>(key)
    }
}

fun LazyPagingItems<*>.isEmpty(): Boolean = with(this.loadState) {
    refresh is LoadState.NotLoading && append.endOfPaginationReached
} && itemCount == 0

@Composable
fun isDarkThemeEnabled(): Boolean {
    val settings by LocalContext.current.settingsDataStore.data.collectAsState(initial = Settings.getDefaultInstance())

    return isSystemInDarkTheme() || settings.darkThemeEnabled
}
