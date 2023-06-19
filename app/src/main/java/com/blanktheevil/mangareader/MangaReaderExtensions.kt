package com.blanktheevil.mangareader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.CoroutineScope

@Composable
fun OnMount(block: suspend CoroutineScope.() -> Unit) =
    LaunchedEffect(Unit, block = block)

fun <T, U> letIfNotNull(
    arg1: T?,
    arg2: U?,
    block: (T, U) -> Unit,
) {
    arg1?.let { t ->
        arg2?.let { u ->
            block(t, u)
        }
    }
}

fun <T, U, V> letIfNotNull(
    arg1: T?,
    arg2: U?,
    arg3: V?,
    block: (T, U, V) -> Unit,
) {
    arg1?.let { t ->
        arg2?.let { u ->
            arg3?.let { v ->
                block(t, u, v)
            }
        }
    }
}