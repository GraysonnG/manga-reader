package com.blanktheevil.mangareader.domain

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.blanktheevil.mangareader.R

enum class LoginError(
    @StringRes private val resId: Int,
): ComposableError {
    INVALID(R.string.login_error),
    ;

    @Composable
    override fun getErrorString(): String = stringResource(id = this.resId)
}