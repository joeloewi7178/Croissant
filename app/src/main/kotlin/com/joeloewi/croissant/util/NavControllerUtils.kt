package com.joeloewi.croissant.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavBackStackEntry

@Composable
fun ViewModelStoreOwner?.navigationIconButton(
    onClick: () -> Unit,
    tint: Color = LocalContentColor.current
): @Composable () -> Unit {
    val currentOnClick by rememberUpdatedState(newValue = onClick)

    return if (this is NavBackStackEntry) {
        {
            IconButton(onClick = currentOnClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = Icons.AutoMirrored.Default.ArrowBack.name,
                    tint = tint
                )
            }
        }
    } else {
        {

        }
    }
}