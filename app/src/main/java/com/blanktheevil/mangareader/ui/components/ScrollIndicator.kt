package com.blanktheevil.mangareader.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.R

@Composable
fun BoxScope.ScrollIndicator(
    scrollState: ScrollState,
) {
    val configuration = LocalConfiguration.current
    val height = configuration.screenHeightDp.dp
    val arrow = painterResource(id = R.drawable.round_keyboard_arrow_down_24)
    val arrowSize = 32.dp
    val showArrow =
        scrollState.canScrollForward &&
                !scrollState.canScrollBackward

    AnimatedVisibility(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .offset(
                y = height - arrowSize - 100.dp
            ),
        visible = showArrow,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Surface(
            Modifier
                .size(arrowSize),
            color = MaterialTheme.colorScheme.background,
            shape = CircleShape,
            shadowElevation = 4.dp
        ) {
            Icon(
                modifier = Modifier.size(arrowSize),
                painter = arrow,
                contentDescription = null
            )
        }
    }
}