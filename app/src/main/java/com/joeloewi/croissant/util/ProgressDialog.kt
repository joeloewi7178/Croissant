package com.joeloewi.croissant.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties

@Composable
fun ProgressDialog(
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {},
        icon = {
            Icon(
                imageVector = Icons.Default.Pending,
                contentDescription = Icons.Default.Pending.name
            )
        },
        title = {
            Text(text = "저장 중")
        },
        text = {
            Text(text = "잠시만 기다려주세요.")
        },
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false
        )
    )
}