package com.blanktheevil.mangareader.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.domain.LoginError
import com.blanktheevil.mangareader.domain.LoginPasswordError
import com.blanktheevil.mangareader.domain.LoginUsernameError
import com.blanktheevil.mangareader.ui.components.InputField
import com.blanktheevil.mangareader.ui.components.MangaReaderTopAppBarState
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.viewmodels.LoginScreenViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    loginScreenViewModel: LoginScreenViewModel = koinViewModel(),
    setTopAppBarState: (MangaReaderTopAppBarState) -> Unit,
    navigateToHome: () -> Unit,
) {
    val uiState by loginScreenViewModel.uiState.collectAsState()
    val errorState = loginScreenViewModel.errorState

    OnMount {
        loginScreenViewModel.initViewModel()
        setTopAppBarState(MangaReaderTopAppBarState(show = false))
    }

    LaunchedEffect(key1 = loginScreenViewModel.currentSession) {
        if (loginScreenViewModel.isSessionValid()) {
            navigateToHome()
        }
    }

    if (loginScreenViewModel.currentSession != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            LoginForm(
                username = uiState.username,
                password = uiState.password,
                setUsername = loginScreenViewModel::setUsername,
                setPassword = loginScreenViewModel::setPassword,
                usernameError = errorState.usernameError,
                passwordError = errorState.passwordError,
                loginError = errorState.loginError,
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
    loginError: LoginError?,
    clearUsernameError: () -> Unit,
    clearPasswordError: () -> Unit,
    login: (onSuccess: () -> Unit) -> Unit,
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        onResult = {}
    )

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(id = R.string.login_title),
            fontSize = 60.sp,
        )

        Text(text = stringResource(id = R.string.login_subtitle))

        AnimatedVisibility(visible = loginError != null) {
            Row(
                Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.error)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Icon(
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    imageVector = Icons.Rounded.Warning,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    text = stringResource(id = R.string.login_error),
                )
            }
        }

        InputField(
            modifier = Modifier.fillMaxWidth(),
            value = username,
            onValueChange = setUsername,
            onFocused = clearUsernameError,
            error = usernameError,
            labelText = stringResource(id = R.string.username_field_label),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Next)
                }
            )
        )

        InputField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            onValueChange = setPassword,
            onFocused = clearPasswordError,
            error = passwordError,
            labelText = stringResource(id = R.string.password_field_label),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    login(navigateToHome)
                }
            )
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { login(navigateToHome) }
        ) {
            Text(text = stringResource(id = R.string.login_button_text))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Don't have an account?")
            TextButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://mangadex.org/")
                    launcher.launch(intent)
                },
                contentPadding = PaddingValues(8.dp),
            ) {
                Text("Sign up here.")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    MangaReaderTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            LoginScreen(navigateToHome = {}, setTopAppBarState = {})
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
                loginError = LoginError.INVALID,
                clearUsernameError = {},
                clearPasswordError = {},
                login = {},
                navigateToHome = {}
            )
        }
    }
}
