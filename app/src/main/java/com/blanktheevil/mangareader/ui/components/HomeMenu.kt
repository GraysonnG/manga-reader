package com.blanktheevil.mangareader.ui.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.dto.UserDto

@Composable
fun HomeUserMenu(
    onLogoutClicked: () -> Unit,
) = Row {
    val context = LocalContext.current
    val mangaDexRepository = MangaDexRepository.getInstance(context)
    var menuOpen by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    val avatar = painterResource(id = R.drawable.avatar)

//    LaunchedEffect(Unit) {
//        val idResult: Result<String> = mangaDexRepository.getUserId()
//        val id = (idResult as? Result.Success<String>)?.data
//
//        val userDataResult = id?.let { mangaDexRepository.getUserData(id) }
//        val userData = (userDataResult as? Result.Success<UserDto>)?.data
//
//        userData?.let { username = it.attributes.username }
//
//        (idResult as? Result.Error)?.let {
//            Toast.makeText(
//                context,
//                it.error.localizedMessage,
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//
//        (userDataResult as? Result.Error)?.let {
//            Toast.makeText(
//                context,
//                it.error.localizedMessage,
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//    }

    IconButton(onClick = {
        menuOpen = true
    }) {
        Icon(painter = avatar, contentDescription = null)
    }

    HomeUserMenuDialog(
        username = username,
        menuOpen = menuOpen,
        avatar = avatar,
        onLogoutClicked = onLogoutClicked,
        onDismissRequest = { menuOpen = false }
    )
}

@Composable
private fun HomeUserMenuDialog(
    username: String,
    menuOpen: Boolean,
    avatar: Painter,
    onLogoutClicked: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    DropdownMenu(
        expanded = menuOpen,
        onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier
                    .clip(CircleShape)
                    .height(75.dp)
                    .width(75.dp),
                painter = avatar,
                contentDescription = ""
            )
            Text(text = username, style = MaterialTheme.typography.titleMedium)
            Button(onClick = onLogoutClicked) {
                Text(text = "Logout")
            }
        }
    }
}