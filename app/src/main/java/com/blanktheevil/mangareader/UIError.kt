package com.blanktheevil.mangareader

interface UIError {
    fun getErrorTitle(): String
    fun getErrorStack(): String
}

class SimpleUIError(
    private val title: String,
    private val throwable: Throwable
): UIError {
    override fun getErrorTitle(): String = title
    override fun getErrorStack(): String = throwable.stackTraceToString()
}