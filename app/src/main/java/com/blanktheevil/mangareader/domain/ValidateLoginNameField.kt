package com.blanktheevil.mangareader.domain

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.blanktheevil.mangareader.R

class ValidateLoginUsernameFieldUseCase {
    operator fun invoke(username: String): LoginUsernameError? = when {
        username.length < 2 -> LoginUsernameError.INVALID
        else -> null
    }
}

enum class LoginUsernameError(
    @StringRes private val resId: Int,
): ComposableError {
    INVALID(R.string.login_name_invalid),
    INCORRECT(R.string.login_username_incorrect)
    ;

    @Composable
    override fun getErrorString() = stringResource(id = this.resId)
}