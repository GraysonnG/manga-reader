package com.blanktheevil.mangareader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

interface UIError {
    fun getErrorTitle(): String
    fun getErrorStack(): String
}

class SimpleUIError(
    private val title: String,
    private val throwable: Throwable
) : UIError {
    override fun getErrorTitle(): String = title
    override fun getErrorStack(): String = throwable.stackTraceToString()
}

@Composable
fun OnUIError(error: UIError?, block: suspend (error: UIError) -> Unit) {
    LaunchedEffect(error, block = {
        error?.let {
            block(it)
        }
    })
}