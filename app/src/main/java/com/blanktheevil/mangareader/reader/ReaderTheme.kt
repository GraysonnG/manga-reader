package com.blanktheevil.mangareader.reader

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.blanktheevil.mangareader.ui.theme.Typography

@Composable
fun ReaderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(),
        typography = Typography,
        content = content,
    )
}