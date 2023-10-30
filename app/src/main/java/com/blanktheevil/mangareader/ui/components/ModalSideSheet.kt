package com.blanktheevil.mangareader.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.DefaultPreview

@Composable
fun ModalSideSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val titleStyle = MaterialTheme.typography.titleLarge
    val contentColor = MaterialTheme.colorScheme.onSurface
    val actionSource by remember {
        mutableStateOf(MutableInteractionSource())
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            modifier = Modifier
                .background(Color.Black.copy(0.5f))
                .fillMaxSize()
                .clickable(
                    interactionSource = actionSource,
                    indication = rememberRipple(color = Color.Transparent),
                ) { onDismissRequest() },
        )
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInHorizontally { it },
        exit = fadeOut() + slideOutHorizontally { it },
    ) {
        Box(
            Modifier.fillMaxSize()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.9f)
                    .align(Alignment.CenterEnd),
                tonalElevation = 16.dp,
                shadowElevation = 32.dp,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    bottomStart = 16.dp,
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(
                            top = 16.dp,
                            end = 16.dp,
                            bottom = 24.dp,
                            start = 16.dp,
                        )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        ProvideTextStyle(value = titleStyle) {
                            CompositionLocalProvider(
                                LocalContentColor provides contentColor,
                                content = title
                            )
                        }
                        IconButton(
                            onClick = { onDismissRequest() }
                        ) {
                            Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
                        }
                    }

                    Box(
                        modifier = Modifier.padding(end = 8.dp, top = 16.dp)
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides contentColor,
                            content = content
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ModalSideSheetPreview() {
    var visible by remember { mutableStateOf(false) }

    DefaultPreview {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box() {
                Button(onClick = { visible = true }) {
                    Text(text = "Show Modal")
                }
            }

            ModalSideSheet(
                visible = visible,
                onDismissRequest = { visible = false },
                title = { Text(text = "Hello World") }
            ) {
                Text(text = "Hello World its me again")
            }
        }
    }
}