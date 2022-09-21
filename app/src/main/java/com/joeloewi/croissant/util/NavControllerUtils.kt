package com.joeloewi.croissant.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

@Composable
fun navigationIconButton(
    previousBackStackEntry: NavBackStackEntry?,
    onClick: () -> Unit
): @Composable () -> Unit {
    val currentOnClick by rememberUpdatedState(newValue = onClick)
    val disappearableIconButton: @Composable (() -> Unit) by remember(previousBackStackEntry) {
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
    navController: NavController,
    key: String
): T? = navController.currentBackStackEntry?.savedStateHandle?.run {
    get<T>(key).apply {
        this@run.remove<T>(key)
    }
}