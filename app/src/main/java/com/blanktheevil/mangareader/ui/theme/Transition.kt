package com.blanktheevil.mangareader.ui.theme

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavBackStackEntry

val slideIn: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition? = {
    slideInHorizontally { it } + fadeIn()
}

val slideOut: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition? = {
    slideOutHorizontally { -it } + fadeOut()
}