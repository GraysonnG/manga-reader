package com.blanktheevil.mangareader.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.rememberLoginState

@Composable
fun HomeUserMenu(
    username: String,
    onLogoutClicked: () -> Unit,
    onLoginClicked: () -> Unit,
) = Row {
    var menuOpen by remember { mutableStateOf(false) }
    val avatar = painterResource(id = R.drawable.round_person_24)

    IconButton(onClick = {
        menuOpen = true
    }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {

            Icon(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.Center),
                painter = avatar,
                contentDescription = null
            )
        }

    }

    HomeUserMenuDialog(
        username = username,
        menuOpen = menuOpen,
        avatar = avatar,
        onLogoutClicked = onLogoutClicked,
        onLoginClicked = onLoginClicked,
        onDismissRequest = { menuOpen = false }
    )
}

@Composable
private fun HomeUserMenuDialog(
    username: String,
    menuOpen: Boolean,
    avatar: Painter,
    onLogoutClicked: () -> Unit,
    onLoginClicked: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val loggedIn by rememberLoginState()

    DropdownMenu(
        expanded = menuOpen,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = avatar,
                    contentDescription = null,
                )
            }
            Text(
                text = username,
                style = MaterialTheme.typography.titleMedium
            )

            if (loggedIn) {
                Button(onClick = {
                    onLogoutClicked()
                    onDismissRequest()
                }) {
                    Text(text = stringResource(id = R.string.home_screen_menu_logout))
                }
            } else {
                Button(onClick = {
                    onLoginClicked()
                    onDismissRequest()
                }) {
                    Text(text = stringResource(id = R.string.home_screen_menu_login))
                }
            }
        }
    }
}