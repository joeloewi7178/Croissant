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
import androidx.navigation.NavController
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
    navController: NavController,
    onClick: (NavController) -> Unit = {
        it.popBackStack()
    }
): @Composable () -> Unit =
    if (navController.previousBackStackEntry != null) {
        {
            IconButton(onClick = { onClick(navController) }) {
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
