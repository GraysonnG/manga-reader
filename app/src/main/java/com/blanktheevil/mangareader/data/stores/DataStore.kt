package com.blanktheevil.mangareader.data.stores

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class DataStore<T: DataStoreState>(
    initialState: T
) {
    protected var _state: MutableStateFlow<T> = MutableStateFlow(initialState)
    val state = _state.asStateFlow()
    var hasRetried = false
        private set
    protected val dataStoreScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

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

abstract class DataStoreState {
    abstract val loading: Boolean
}