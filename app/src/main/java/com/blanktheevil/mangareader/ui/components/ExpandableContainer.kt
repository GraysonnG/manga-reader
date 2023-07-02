package com.blanktheevil.mangareader.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ExpandableContainer(
    title: @Composable () -> Unit,
    onExpand: suspend () -> Boolean = { true },
    startExpanded: Boolean = false,
    content: @Composable () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var waiting by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val transition = updateTransition(expanded, label="")
    val arrowRotationDegree by transition.animateFloat(
        label = "",
        transitionSpec = {
            tween()
        },
        targetValueByState = { if (it) 90f else 0f }
    )
    LaunchedEffect(Unit) {
        if (startExpanded) {
            waiting = true
            val cont = onExpand()
            if (cont) {
                expanded = true
            }
            waiting = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.tertiaryContainer)
                    .fillMaxWidth()
                    .clickable {
                        coroutineScope.launch {
                            if (!expanded) {
                                waiting = true
                                val cont = onExpand()
                                if (cont) {
                                    expanded = true
                                }
                                waiting = false
                            } else {
                                expanded = false
                            }
                        }
                    }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.height(24.dp).rotate(arrowRotationDegree),
                        imageVector = ImageVector.vectorResource(id = R.drawable.navigate_next),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    title()
                }

                CircularProgressIndicator(
                    modifier = Modifier
                        .scale(0.5f)
                        .alpha(if (waiting) 1f else 0f),
                )

            }
            ExpandableContent(
                visible = expanded,
                initialVisibility = startExpanded,
                content = content
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ExpandableContent(
    visible: Boolean = true,
    initialVisibility: Boolean = false,
    content: @Composable () -> Unit,
) {
    val enterTransition = remember {
        expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween()
        ) + fadeIn(
            initialAlpha = 0.3f,
            animationSpec = tween()
        )
    }
    val exitTransition = remember {
        shrinkVertically(
            // Expand from the top.
            shrinkTowards = Alignment.Top,
            animationSpec = tween()
        ) + fadeOut(
            // Fade in with the initial alpha of 0.3f.
            animationSpec = tween()
        )
    }
    AnimatedVisibility(
        visible = visible,
        initiallyVisible = initialVisibility,
        enter = enterTransition,
        exit = exitTransition
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLight() {
    MangaReaderTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ExpandableContainer(
                    title = { Text("Text Container") },
                    onExpand = {
                        delay(2000)
                        true
                    }
                ) {
                    Text(text = "Expanded Content goes here\n\nBig Content")
                }

                ExpandableContainer(
                    title = { Text("Text Container") },
                    startExpanded = true,
                    onExpand = {
                        delay(2000)
                        true
                    }
                ) {
                    Text(text = "Expanded Content goes here")
                }
            }
        }
    }
}