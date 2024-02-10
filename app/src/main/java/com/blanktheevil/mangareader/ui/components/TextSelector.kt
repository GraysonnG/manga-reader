package com.blanktheevil.mangareader.ui.components

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.PopupProperties
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.ui.mediumDp
import com.blanktheevil.mangareader.ui.smallDp
import com.blanktheevil.mangareader.ui.smallPadding
import java.util.Locale

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TextSelector(
    modifier: Modifier = Modifier,
    placeholder: String,
    valueMap: Map<String, String>,
    selectedValues: List<String>,
    onValueSelected: (key: String) -> Unit,
    onClear: (() -> Unit)? = null,
) {
    var text by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var hasFocus by remember { mutableStateOf(false) }
    val locale = LocalContext.current.resources.configuration.locales[0]
    val outlinedCircle = painterResource(id = R.drawable.outline_circle_24)
    val filledCircle = painterResource(id = R.drawable.filled_circle_24)
    val arrowRotation by animateFloatAsState(
        targetValue = if (hasFocus) 0f else -90f,
        label = ""
    )
    val focusManager = LocalFocusManager.current

    val trailingIcon = @Composable {
        if (!expanded && selectedValues.isNotEmpty() && onClear != null) {
            IconButton(onClick = { onClear() }) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null
                )
            }
        } else {
            IconButton(onClick = { expanded = !expanded }) {
                // this will change to an x when a value is present
                Icon(
                    modifier = Modifier.rotate(arrowRotation),
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null
                )
            }
        }
    }

    fun String.cap(locale: Locale): String {
        return this.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                locale
            ) else it.toString()
        }
    }

    Column(modifier.onFocusChanged {
        expanded = it.hasFocus
        hasFocus = it.hasFocus
    }) {
        MangaReaderTextField(
            value = if (!hasFocus) selectedValues.map {
                valueMap[it]
            }.joinToString(", ") else text,
            onValueChange = {
                expanded = true
                text = it
            },
            placeholder = {
                Text(
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    text = placeholder,
                )
            },
            trailingIcon = trailingIcon,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Ascii,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Next)
                }
            )
        )
        DropdownMenu(
            modifier = Modifier.width(IntrinsicSize.Max),
            expanded = expanded || hasFocus,
            onDismissRequest = { expanded = false },
            properties = PopupProperties(
                focusable = false,
                usePlatformDefaultWidth = true
            )
        ) {
            val validItems = valueMap.entries
                .toList()
                .filter { (_, v) -> v.contains(text, true) }

            val displayItems = validItems.ifEmpty { valueMap.entries.toList() }

            displayItems.forEach { (k, v) ->
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.size(mediumDp),
                            painter = if (k in selectedValues) filledCircle else outlinedCircle,
                            contentDescription = null,
                            tint = if (k in selectedValues) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    },
                    text = { Text(v.cap(locale)) },
                    onClick = { onValueSelected(k) },
                )
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewTextSelector() {
    var selectedValues: List<String> by remember {
        mutableStateOf(emptyList())
    }

    val valueMap = mapOf(
        "value1" to "Anime",
        "value2" to "Manga",
        "value3" to "Comedy",
        "value4" to "Romance",
        "value5" to "Shounen",
    )

    DefaultPreview {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .smallPadding()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(smallDp)
            ) {
                TextSelector(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    placeholder = "Placeholder...",
                    valueMap = valueMap,
                    selectedValues = selectedValues,
                    onValueSelected = {
                        selectedValues = if (it in selectedValues) {
                            selectedValues.filterNot { s -> s == it }
                        } else {
                            selectedValues + it
                        }
                    },
                )
                TextSelector(
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = "Placeholder...",
                    valueMap = valueMap,
                    selectedValues = selectedValues,
                    onValueSelected = {
                        selectedValues = if (it in selectedValues) {
                            selectedValues.filterNot { s -> s == it }
                        } else {
                            selectedValues + it
                        }
                    },
                    onClear = { selectedValues = emptyList() }
                )
            }
        }
    }
}