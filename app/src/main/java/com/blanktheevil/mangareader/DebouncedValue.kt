package com.blanktheevil.mangareader

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DebouncedValue<T>(
    initialValue: T,
    private val debounceTime: Long,
    private val coroutineScope: CoroutineScope,
) {
    private val valueFlow = MutableStateFlow(initialValue)
    private var job: Job? = null

    var value: T
        get() = valueFlow.value
        set(newValue) {
            job?.cancel()
            job = coroutineScope.launch {
                delay(debounceTime)
                valueFlow.emit(newValue)
            }
        }

    fun asStateFlow(): StateFlow<T> = valueFlow
}