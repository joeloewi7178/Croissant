package com.joeloewi.croissant.util

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import com.joeloewi.croissant.R

@Composable
fun ProgressDialog(
    title: @Composable () -> Unit = {
        Text(text = stringResource(id = R.string.saving))
    }
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {},
        icon = {
            Icon(
                imageVector = Icons.Default.Pending,
                contentDescription = Icons.Default.Pending.name
            )
        },
        title = title,
        text = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.wait_for_a_moment),
                textAlign = TextAlign.Center
            )
        },
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false
        )
    )
}