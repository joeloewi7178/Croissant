/*
 *    Copyright 2024. joeloewi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.joeloewi.croissant.core.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.HalfDp
import com.joeloewi.croissant.ui.theme.IconDp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RedemptionCodeListItem(
    modifier: Modifier,
    isExpanded: Boolean,
    item: Pair<HoYoLABGame, AnnotatedString>,
    onClickUrl: (annotatedString: AnnotatedString, offset: Int) -> Unit,
    onClickExpand: (hoYoLABGame: HoYoLABGame) -> Unit
) {
    val foldedHeight = 216.dp
    val textStyle = LocalTextStyle.current
    val textColor = textStyle.color.takeOrElse {
        LocalContentColor.current
    }
    val density = LocalDensity.current
    val contentTextMeasurer = rememberTextMeasurer()
    val isContentCanBeFolded by remember {
        derivedStateOf {
            with(density) {
                contentTextMeasurer.measure(
                    text = item.second,
                    style = textStyle.copy(color = textColor)
                ).size.height.toDp()
            } > foldedHeight
        }
    }

    Card(
        modifier = modifier
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearEasing
                )
            )
            .fillMaxWidth()
            .padding(horizontal = DefaultDp, vertical = HalfDp),
    ) {
        Column {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                navigationIcon = {
                    AsyncImage(
                        modifier = Modifier
                            .padding(12.dp)
                            .size(IconDp)
                            .clip(MaterialTheme.shapes.extraSmall),
                        model = item.first.gameIconUrl,
                        contentDescription = null
                    )
                },
                title = {
                    Text(text = stringResource(id = item.first.gameNameStringResId()))
                },
                actions = {
                    if (isContentCanBeFolded) {
                        IconButton(
                            onClick = { onClickExpand(item.first) }
                        ) {
                            if (isExpanded) {
                                Icon(
                                    imageVector = Icons.Default.ExpandLess,
                                    contentDescription = Icons.Default.ExpandLess.name
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.ExpandMore,
                                    contentDescription = Icons.Default.ExpandMore.name
                                )
                            }
                        }
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0)
            )

            Row(
                modifier = Modifier
                    .composed {
                        val measureHeight by remember(
                            isContentCanBeFolded,
                            isExpanded,
                            item.first
                        ) {
                            derivedStateOf {
                                if (!isContentCanBeFolded) {
                                    return@derivedStateOf Dp.Unspecified
                                }

                                if (isExpanded) {
                                    return@derivedStateOf Dp.Unspecified
                                }

                                return@derivedStateOf foldedHeight
                            }
                        }

                        remember(measureHeight) {
                            height(measureHeight)
                        }
                    }
                    .padding(horizontal = DefaultDp),
            ) {
                SelectionContainer {
                    ClickableText(
                        text = item.second,
                        style = textStyle.copy(color = textColor),
                        onClick = { offset -> onClickUrl(item.second, offset) }
                    )
                }
            }
        }
    }
}