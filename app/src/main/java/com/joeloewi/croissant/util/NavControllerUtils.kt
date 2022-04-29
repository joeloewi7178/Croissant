package com.joeloewi.croissant.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

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