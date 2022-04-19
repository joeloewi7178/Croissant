package com.joeloewi.croissant.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> rememberFlow(
    flow: Flow<T>,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
): Flow<T> {
    return remember(
        key1 = flow,
        key2 = lifecycleOwner
    ) { flow.flowWithLifecycle(lifecycleOwner.lifecycle) }
}

@Composable
fun navigationIconButton(
    previousBackStackEntry: NavBackStackEntry?,
    onClick: () -> Unit
): @Composable () -> Unit =
    if (previousBackStackEntry != null) {
        {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = Icons.Outlined.ArrowBack.name
                )
            }
        }
    } else {
        {

        }
    }

fun LazyPagingItems<*>.isEmpty(): Boolean = with(this.loadState) {
    refresh is LoadState.NotLoading && append.endOfPaginationReached
} && itemCount == 0
