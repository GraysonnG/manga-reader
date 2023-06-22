package com.blanktheevil.mangareader.domain

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import com.blanktheevil.mangareader.data.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import java.lang.reflect.Method

abstract class DataStore<T>(
    initialState: T
) {
    protected var _state: MutableStateFlow<T> = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    abstract fun get(viewModelScope: CoroutineScope)

    @Composable
    operator fun invoke(): State<T> = state.collectAsState()
}