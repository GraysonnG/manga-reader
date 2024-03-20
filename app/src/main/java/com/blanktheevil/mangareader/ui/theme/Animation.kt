package com.blanktheevil.mangareader.ui.theme

import androidx.compose.animation.core.spring

fun <T> springGentle() = spring<T>(
    stiffness = 100f,
    dampingRatio = 0.75f,
)

fun <T> springQuick() = spring<T>(
    stiffness = 300f,
    dampingRatio = 1.118f,
)

fun <T> springBouncy() = spring<T>(
    stiffness = 600f,
    dampingRatio = 0.306f,
)

fun <T> springSlow() = spring<T>(
    stiffness = 80f,
    dampingRatio = 1.118f,
)