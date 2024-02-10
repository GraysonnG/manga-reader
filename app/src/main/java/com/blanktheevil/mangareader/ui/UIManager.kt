package com.blanktheevil.mangareader.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import com.blanktheevil.mangareader.ui.components.MangaReaderTopAppBarState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.compose.koinInject

class UIManager {
    private val _topAppBarState = MutableStateFlow(MangaReaderTopAppBarState())
    val topAppBarState = _topAppBarState.asStateFlow()

    fun setTopAppBarState(state: MangaReaderTopAppBarState) {
        _topAppBarState.value = state
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun setTopAppBarState(state: MangaReaderTopAppBarState) {
    val uiManager: UIManager = koinInject()
    uiManager.setTopAppBarState(state)
}