package com.joeloewi.croissant.ui.navigation.main.attendances.screen.createattendance.composable

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.joeloewi.croissant.R
import com.joeloewi.croissant.ui.theme.DefaultDp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GetSession(
    modifier: Modifier = Modifier,
    onLoginHoYoLAB: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val responsiveHorizontalPadding by remember(configuration) {
        derivedStateOf { (configuration.screenWidthDp * 0.2).dp }
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DefaultDp)
                    .navigationBarsPadding(),
                onClick = onLoginHoYoLAB
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        space = DefaultDp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.Login,
                        contentDescription = Icons.AutoMirrored.Default.Login.name
                    )
                    Text(
                        text = stringResource(id = R.string.go_to_hoyolab),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.systemBars.exclude(WindowInsets.statusBars)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = DefaultDp),
            verticalArrangement = Arrangement.spacedBy(space = DefaultDp)
        ) {
            Text(
                text = stringResource(id = R.string.get_session),
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = stringResource(id = R.string.login_hoyolab),
                style = MaterialTheme.typography.titleMedium
            )

            HorizontalPager(
                state = rememberPagerState { 3 },
                contentPadding = PaddingValues(horizontal = responsiveHorizontalPadding),
                key = { it }
            ) { page ->

                @DrawableRes
                val drawableResId = when (page) {
                    0 -> {
                        R.drawable.login_guide_1
                    }

                    1 -> {
                        R.drawable.login_guide_2
                    }

                    2 -> {
                        R.drawable.login_guide_3
                    }

                    else -> {
                        R.drawable.image_placeholder
                    }
                }

                AsyncImage(
                    modifier = Modifier
                        .padding(DefaultDp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    model = drawableResId,
                    contentDescription = null
                )
            }

            Text(
                text = stringResource(id = R.string.get_session_description_1),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = stringResource(id = R.string.get_session_description_2),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = stringResource(id = R.string.get_session_description_3),
                style = MaterialTheme.typography.bodyMedium
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(DefaultDp),
                ) {
                    Icon(
                        modifier = Modifier.padding(DefaultDp),
                        imageVector = Icons.Default.Star,
                        contentDescription = Icons.Default.Star.name
                    )
                    Text(
                        modifier = Modifier.padding(DefaultDp),
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(stringResource(id = R.string.note))
                                append(": ")
                            }
                            append(stringResource(id = R.string.experimental_sns_login_feature))
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}