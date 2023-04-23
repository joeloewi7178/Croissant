package com.joeloewi.croissant.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

@Composable
fun navigationIconButton(
    previousBackStackEntry: NavBackStackEntry?,
    onClick: () -> Unit
): @Composable () -> Unit {
    val currentOnClick by rememberUpdatedState(newValue = onClick)
    val disappearableIconButton: @Composable (() -> Unit) by remember(
        previousBackStackEntry,
        currentOnClick
    ) {
        derivedStateOf {
            if (previousBackStackEntry != null) {
                {
                    IconButton(onClick = currentOnClick) {
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
        }
    }

    return disappearableIconButton
}


fun <T> getResultFromPreviousComposable(
    navController: NavHostController,
    key: String
): T? = navController.currentBackStackEntry?.savedStateHandle?.run {
    get<T>(key).apply {
        this@run.remove<T>(key)
    }
}