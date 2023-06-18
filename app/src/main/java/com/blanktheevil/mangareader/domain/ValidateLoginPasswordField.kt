package com.blanktheevil.mangareader.domain

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.blanktheevil.mangareader.R

class ValidateLoginPasswordField {
    operator fun invoke(password: String) = when {
        password.length < 8 -> LoginPasswordError.INVALID
        else -> null
    }
}

enum class LoginPasswordError(
    @StringRes private val resId: Int
): ComposableError {
    INVALID(R.string.login_password_invalid),
    INCORRECT(R.string.login_password_incorrect)
    ;

    @Composable
    override fun getErrorString() = stringResource(id = this.resId)
}