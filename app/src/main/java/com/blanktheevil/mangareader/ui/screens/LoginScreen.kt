package com.blanktheevil.mangareader.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.domain.LoginPasswordError
import com.blanktheevil.mangareader.domain.LoginUsernameError
import com.blanktheevil.mangareader.ui.components.InputField
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.ui.theme.Typography
import com.blanktheevil.mangareader.viewmodels.LoginScreenViewModel

@Composable
fun LoginScreen(
    loginScreenViewModel: LoginScreenViewModel = viewModel(),
    navigateToHome: () -> Unit,
) {
    loginScreenViewModel.initViewModel(context = LocalContext.current)
    val uiState by loginScreenViewModel.uiState.collectAsState()
    val errorState = loginScreenViewModel.errorState

    LaunchedEffect(key1 = loginScreenViewModel.currentSession) {
        if (loginScreenViewModel.isSessionValid()) {
            navigateToHome()
        }
    }

    if (loginScreenViewModel.currentSession != null) {
        Box(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            LoginForm(
                username = uiState.username,
                password = uiState.password,
                setUsername = loginScreenViewModel::setUsername,
                setPassword = loginScreenViewModel::setPassword,
                usernameError = errorState.usernameError,
                passwordError = errorState.passwordError,
                clearUsernameError = loginScreenViewModel::clearUsernameError,
                clearPasswordError = loginScreenViewModel::clearPasswordError,
                login = loginScreenViewModel::login,
                navigateToHome = navigateToHome,
            )
        }
    }
}

@Composable
private fun LoginForm(
    username: String,
    password: String,
    setUsername: (String) -> Unit,
    setPassword: (String) -> Unit,
    usernameError: LoginUsernameError?,
    passwordError: LoginPasswordError?,
    clearUsernameError: () -> Unit,
    clearPasswordError: () -> Unit,
    login: (onSuccess: () -> Unit) -> Unit,
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Login",
            style = Typography.titleLarge
        )

        Text(text = "Enter your Mangadex credentials.")


        InputField(
            modifier = Modifier.fillMaxWidth(),
            value = username,
            onValueChange = setUsername,
            onFocused = clearUsernameError,
            error = usernameError,
            labelText = stringResource(id = R.string.username_field_label)
        )

        InputField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            onValueChange = setPassword,
            onFocused = clearPasswordError,
            error = passwordError,
            labelText = stringResource(id = R.string.username_field_label),
            visualTransformation = PasswordVisualTransformation(),
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { login(navigateToHome) }
        ) {
            Text(text = "Login")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    MangaReaderTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            LoginScreen(navigateToHome = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewForm() {
    MangaReaderTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LoginForm(
                username = "testuser",
                password = "testuserpass",
                setUsername = {},
                setPassword = {},
                usernameError = LoginUsernameError.INVALID,
                passwordError = null,
                clearUsernameError = {},
                clearPasswordError = {},
                login = {},
                navigateToHome = {}
            )
        }
    }
}
