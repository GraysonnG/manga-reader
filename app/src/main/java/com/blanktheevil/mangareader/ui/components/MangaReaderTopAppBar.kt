@file:OptIn(ExperimentalMaterial3Api::class)

package com.blanktheevil.mangareader.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.ui.theme.MangaReaderDefaults

@Composable
fun MangaReaderTopAppBar(
    colored: Boolean = true,
    title: String,
    titleIcon: Painter?,
    actions: @Composable (RowScope.() -> Unit) = {},
    navigateBack: (() -> Unit)? = null,
    show: Boolean = true,
) = AnimatedVisibility(
    visible = show,
    enter = expandVertically(expandFrom = Alignment.Top) { it },
    exit = shrinkVertically(shrinkTowards = Alignment.Top) { it },
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                titleIcon?.let {
                    Icon(painter = titleIcon, contentDescription = null)
                }
                Text(text = title)
            }
        },
        colors = if (colored) {
            mangaReaderTopAppBarDefaultColors()
        } else {
            TopAppBarDefaults.topAppBarColors()
        },
        navigationIcon = {
            if (navigateBack != null) {
                MangaReaderDefaults.BackArrowIconButton(
                    onClick = navigateBack
                )
            }
        },
        actions = actions,
    )
}

@Composable
fun MangaReaderTopAppBar(
    mangaReaderTopAppBarState: MangaReaderTopAppBarState,
) {
    MangaReaderTopAppBar(
        colored = mangaReaderTopAppBarState.colored,
        title = mangaReaderTopAppBarState.title,
        titleIcon = mangaReaderTopAppBarState.titleIcon,
        actions = mangaReaderTopAppBarState.actions,
        navigateBack = mangaReaderTopAppBarState.navigateBack,
        show = mangaReaderTopAppBarState.show,
    )
}

@Composable
fun mangaReaderTopAppBarDefaultColors() = TopAppBarDefaults.topAppBarColors(
    containerColor = MaterialTheme.colorScheme.primary,
    scrolledContainerColor = MaterialTheme.colorScheme.primary,
    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
    titleContentColor = MaterialTheme.colorScheme.onPrimary,
    actionIconContentColor = Color.Unspecified,
)

@Stable
data class MangaReaderTopAppBarState(
    val colored: Boolean = true,
    val title: String = "",
    val titleIcon: Painter? = null,
    val actions: @Composable (RowScope.() -> Unit) = {},
    val navigateBack: (() -> Unit)? = null,
    val show: Boolean = true,
)

@Composable
fun rememberMangaReaderTopAppBarState(
    colored: Boolean = true,
    title: String = "",
    titleIcon: Painter? = null,
    actions: @Composable (RowScope.() -> Unit) = {},
    navigateBack: (() -> Unit)? = null,
): MutableState<MangaReaderTopAppBarState> {
    return remember { mutableStateOf(
        MangaReaderTopAppBarState(
            colored = colored,
            title = title,
            titleIcon = titleIcon,
            actions = actions,
            navigateBack = navigateBack,
        )
    ) }
}