package com.blanktheevil.mangareader.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    
    Box(modifier = Modifier.fillMaxSize())
}