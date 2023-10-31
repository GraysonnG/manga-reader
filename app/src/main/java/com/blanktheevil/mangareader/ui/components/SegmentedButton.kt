package com.blanktheevil.mangareader.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import com.blanktheevil.mangareader.ui.xSmallPaddingVertical

@Composable
fun SegmentedButton(
    modifier: Modifier = Modifier,
    options: List<String>,
    initialSelectedIndex: Int = 0,
    onSelected: (Int) -> Unit,
) {
    var selectedIndex by remember { mutableStateOf(initialSelectedIndex) }

    LaunchedEffect(initialSelectedIndex) {
        selectedIndex = initialSelectedIndex
    }

    Surface(
        modifier = Modifier.xSmallPaddingVertical(),
        color = Color.Transparent,
        contentColor = Color.Unspecified,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(30.dp)
    ) {
        Row(
            modifier = modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
        ) {
            options.forEachIndexed { index, it ->
                val isSelected = index == selectedIndex

                Row(
                    modifier = Modifier
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = rememberRipple(
                                color = Color.White,
                            ),
                        ) {
                            onSelected(index)
                            selectedIndex = index
                        }
                        .weight(1f, fill = true)
                        .then(
                            if (isSelected) Modifier.background(
                                MaterialTheme.colorScheme.surfaceVariant
                            )
                            else Modifier
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isSelected) {
                        Icon(
                            Icons.Rounded.Check,
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(end = 4.dp),
                        )
                    }

                    CompositionLocalProvider(
                        LocalTextStyle provides MaterialTheme.typography.labelLarge,
                        LocalContentColor provides MaterialTheme.colorScheme.onSurface,
                    ) {
                        Text(
                            text = it.replace(" ", ""),
                            maxLines = 1,
                        )
                    }
                }

                if (index != options.lastIndex) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.outlineVariant)
                            .width(1.dp)
                            .fillMaxHeight()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SegmentedButtonPreview() {
    DefaultPreview {
        Surface(Modifier.fillMaxSize()) {
            Column() {
                OutlinedButton(onClick = { /*TODO*/ }) {
                    Text("Hello")
                }
                SegmentedButton(
                    options = listOf("Follows", "Longer Longer Text"),
                    onSelected = {},
                )
                SegmentedButton(
                    options = listOf("item1", "item2", "item3"),
                    onSelected = {},
                )
            }
        }
    }
}