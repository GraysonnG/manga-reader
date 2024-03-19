package com.blanktheevil.mangareader.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.session.Session
import com.blanktheevil.mangareader.domain.LoginError
import com.blanktheevil.mangareader.domain.LoginPasswordError
import com.blanktheevil.mangareader.domain.LoginUsernameError
import com.blanktheevil.mangareader.domain.ValidateLoginPasswordField
import com.blanktheevil.mangareader.domain.ValidateLoginUsernameFieldUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginScreenState(
    val username: String = "",
    val password: String = "",
    val loading: Boolean = true,
)

data class LoginScreenErrorState(
    val usernameError: LoginUsernameError? = null,
    val passwordError: LoginPasswordError? = null,
    val loginError: LoginError? = null,
) {
    fun hasNoErrors(): Boolean {
        return usernameError == null && passwordError == null
    }
}

class LoginScreenViewModel(
    private val mangaDexRepository: MangaDexRepository,
) : ViewModel() {
    private val validateUsernameUseCase: ValidateLoginUsernameFieldUseCase =
        ValidateLoginUsernameFieldUseCase()
    private val validatePasswordUseCase: ValidateLoginPasswordField =
        ValidateLoginPasswordField()

    private val _uiState = MutableStateFlow(LoginScreenState())
    var uiState: StateFlow<LoginScreenState> = _uiState.asStateFlow()
    var errorState by mutableStateOf(LoginScreenErrorState())
        private set

    var currentSession: Session? = null

    fun initViewModel() {
        viewModelScope.launch {
            currentSession = mangaDexRepository.getSession()
            _uiState.value = _uiState.value.copy(loading = false)
        }
    }

    fun login(onContinue: () -> Unit) {
        validateFields()

        if (errorState.hasNoErrors()) {
            errorState = errorState.copy(loginError = null)
            viewModelScope.launch {
                val result = mangaDexRepository.login(
                    _uiState.value.username,
                    _uiState.value.password
                )
                currentSession = if (result is Result.Success) {
                    onContinue()
                    result.data
                } else {
                    errorState = errorState.copy(
                        loginError = LoginError.INVALID,
                    )
                    _uiState.value = _uiState.value.copy(password = "")
                    null
                }
            }
        }
    }

    fun isSessionValid(): Boolean {
        return currentSession != null
    }

    fun setUsername(username: String) {
        _uiState.value = _uiState.value.copy(username = username)
    }

    fun clearUsernameError() {
        errorState = errorState.copy(
            usernameError = null
        )
    }

    fun setPassword(password: String) {
        _uiState.value = uiState.value.copy(password = password)
    }

    fun clearPasswordError() {
        errorState = errorState.copy(
            passwordError = null
        )
    }

    private fun validateFields() {
        errorState = errorState.copy(
            usernameError = validateUsernameUseCase(_uiState.value.username),
            passwordError = validatePasswordUseCase(_uiState.value.password),
        )
    }
}