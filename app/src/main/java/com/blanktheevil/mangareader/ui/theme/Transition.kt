package com.blanktheevil.mangareader.ui.theme

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavBackStackEntry

@OptIn(ExperimentalAnimationApi::class)
val slideIn: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition? = {
    slideInHorizontally { it }+ fadeIn()
}

@OptIn(ExperimentalAnimationApi::class)
val slideOut: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition? = {
    slideOutHorizontally { -it } + fadeOut()
}