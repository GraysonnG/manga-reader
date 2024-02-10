package com.blanktheevil.mangareader.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.blanktheevil.mangareader.LocalScrollState

@Composable
fun ScrollableBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val scrollState = LocalScrollState.current

    Box(
        modifier = modifier.verticalScroll(scrollState)
    ) {
        content()
    }
}