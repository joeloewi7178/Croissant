package com.joeloewi.croissant.ui.common

import androidx.compose.material.SwitchDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.compositeOver

@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?
) {
    //switch in material 3 does not animate value changing
    //when onCheckedChange is null

    val checkedThumbColor = MaterialTheme.colorScheme.primary
    val checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
    val uncheckedThumbColor = MaterialTheme.colorScheme.onSurface
    val uncheckedTrackColor = MaterialTheme.colorScheme.outline

    androidx.compose.material.Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = checkedThumbColor,
            checkedTrackColor = checkedTrackColor,
            uncheckedThumbColor = uncheckedThumbColor,
            uncheckedTrackColor = uncheckedTrackColor,
            disabledCheckedThumbColor = checkedThumbColor
                .copy(alpha = 0.38f)
                .compositeOver(MaterialTheme.colorScheme.surface),
            disabledCheckedTrackColor = checkedTrackColor
                .copy(alpha = 0.12f)
                .compositeOver(MaterialTheme.colorScheme.surface),
            disabledUncheckedThumbColor = uncheckedThumbColor
                .copy(alpha = 0.38f)
                .compositeOver(MaterialTheme.colorScheme.surface),
            disabledUncheckedTrackColor = uncheckedTrackColor
                .copy(alpha = 0.12f)
                .compositeOver(MaterialTheme.colorScheme.surface)
        )
    )
}