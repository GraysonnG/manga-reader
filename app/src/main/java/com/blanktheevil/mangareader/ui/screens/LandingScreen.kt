package com.blanktheevil.mangareader.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.data.MangaDexRepository

@Composable
fun LandingScreen(
    navigateToHome: () -> Unit,
    navigateToLogin: () -> Unit,
) {
    val context = LocalContext.current

    OnMount {
        val mangaDexRepository = MangaDexRepository.getInstance(context)
        val session = mangaDexRepository.getSession()
        if (session != null) {
            navigateToHome()
        } else {
            navigateToLogin()
        }
    }
}