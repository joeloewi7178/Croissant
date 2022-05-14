package com.joeloewi.croissant.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.joeloewi.croissant.R

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
            Text(text = stringResource(id = R.string.saving))
        },
        text = {
            Text(text = stringResource(id = R.string.wait_for_a_moment))
        },
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false
        )
    )
}