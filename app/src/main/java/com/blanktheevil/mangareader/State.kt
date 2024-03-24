package com.blanktheevil.mangareader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun <T> rememberState(initial: T): MutableState<T> {
    return remember { mutableStateOf(initial) }
}

@Composable
fun rememberFalse(): MutableState<Boolean> {
    return remember { mutableStateOf(false) }
}

@Composable
fun rememberTrue(): MutableState<Boolean> {
    return remember { mutableStateOf(true) }
}