package com.blanktheevil.mangareader.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.blanktheevil.mangareader.LocalNavController
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.navigation.navigateToHome

@Composable
fun LandingScreen() {
    val navController = LocalNavController.current

    OnMount {
        navController.navigateToHome()
    }

    Box(modifier = Modifier.fillMaxSize())
}