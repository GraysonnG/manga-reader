package com.blanktheevil.mangareader.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.data.MangaDexRepository
import org.koin.compose.koinInject

@Composable
fun LandingScreen(
    navigateToHome: () -> Unit,
    navigateToLogin: () -> Unit,
) {
    val mangaDexRepository: MangaDexRepository = koinInject()

    OnMount {
        val session = mangaDexRepository.getSession()
        if (session != null) {
            navigateToHome()
        } else {
            navigateToLogin()
        }
    }

    Box(modifier = Modifier.fillMaxSize())
}