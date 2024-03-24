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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.ui.RoundedCornerLarge
import com.blanktheevil.mangareader.ui.SpacerSmall
import com.blanktheevil.mangareader.ui.mediumDp
import com.blanktheevil.mangareader.ui.mediumPadding
import com.blanktheevil.mangareader.ui.smallPadding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ExpandableContainer(
    modifier: Modifier = Modifier,
    title: @Composable RowScope.() -> Unit,
    background: (@Composable BoxScope.() -> Unit)? = null,
    shape: Shape = RoundedCornerLarge,
    onExpand: suspend () -> Boolean = { true },
    startExpanded: Boolean = false,
    titleContentColor: Color = LocalContentColor.current,
    content: @Composable () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var waiting by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val transition = updateTransition(expanded, label = "")
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
        shape = shape,
        modifier = modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    ) {
        Column(Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                    .height(IntrinsicSize.Min)
            ) {
                background?.invoke(this)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
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
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CompositionLocalProvider(value = LocalContentColor provides titleContentColor) {
                        ExpandableListTitle(
                            arrowRotationDegree = arrowRotationDegree,
                            title = title,
                            waiting = waiting
                        )
                    }
                }
            }

            ExpandableContent(
                visible = expanded,
                initialVisibility = startExpanded,
                content = content
            )
        }
    }
}

@Composable
private fun RowScope.ExpandableListTitle(
    arrowRotationDegree: Float,
    title: @Composable RowScope.() -> Unit,
    waiting: Boolean,
) {
    Icon(
        modifier = Modifier
            .height(24.dp)
            .rotate(arrowRotationDegree),
        imageVector = ImageVector.vectorResource(id = R.drawable.navigate_next),
        contentDescription = null,
        tint = LocalContentColor.current
    )
    SpacerSmall()
    Row(Modifier.weight(1f)) {
        title()
    }
    SpacerSmall()
    if (waiting) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(24.dp)
                .scale(0.75f),
            color = LocalContentColor.current,
            strokeWidth = 2.dp
        )
    } else {
        Spacer(modifier = Modifier.size(24.dp))
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
        Column(
            modifier = Modifier
                .smallPadding()
                .padding(bottom = mediumDp)
        ) {
            content()
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewLight() {
    DefaultPreview {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .mediumPadding(),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(mediumDp),
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
                        delay(5000)
                        true
                    }
                ) {
                    Text(text = "Expanded Content goes here")
                }

                ExpandableContainer(
                    title = { Text("Text Container") },
                    background = {
                        Box(
                            Modifier
                                .height(128.dp)
                                .fillMaxWidth()
                                .background(color = Color.Red)
                        )
                    },
                    startExpanded = true,
                    onExpand = {
                        delay(2000)
                        true
                    }
                ) {
                    Text(text = "Expanded Content goes here")
                }

                ExpandableContainer(
                    title = { Text("Text Container But The Text is Really Long For Some Reason") },
                    onExpand = {
                        delay(2000)
                        true
                    }
                ) {
                    Text(text = "Expanded Content goes here\n\nBig Content")
                }
            }
        }
    }
}