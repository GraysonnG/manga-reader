package com.blanktheevil.mangareader.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.blanktheevil.mangareader.LocalNavController
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.navigation.navigateToHome
import com.blanktheevil.mangareader.navigation.navigateToLogin
import org.koin.compose.koinInject

@Composable
fun LandingScreen() {
    val mangaDexRepository: MangaDexRepository = koinInject()
    val navController = LocalNavController.current

    OnMount {
        val session = mangaDexRepository.getSession()
        if (session != null) {
            navController.navigateToHome()
        } else {
            navController.navigateToLogin()
        }
    }

    Box(modifier = Modifier.fillMaxSize())
}