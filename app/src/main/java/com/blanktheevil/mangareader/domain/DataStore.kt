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
    var hasRetried = false
        private set

    abstract fun get()
    protected abstract fun onRefresh()

    fun retry() {
        if (!hasRetried) {
            hasRetried = true
            get()
        }
    }

    fun refresh() {
        onRefresh()
        get()
    }

    @Composable
    operator fun invoke(): State<T> = state.collectAsState()
}