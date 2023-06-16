package com.blanktheevil.mangareader.domain

import androidx.compose.runtime.Composable

interface ComposableError {
    @Composable
    fun getErrorString(): String
}