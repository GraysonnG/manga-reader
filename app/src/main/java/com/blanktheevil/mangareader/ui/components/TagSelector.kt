package com.blanktheevil.mangareader.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.Tag
import com.blanktheevil.mangareader.data.TagsMode
import com.blanktheevil.mangareader.ui.SpacerSmall
import com.blanktheevil.mangareader.ui.mediumDp
import com.blanktheevil.mangareader.ui.smallDp
import com.blanktheevil.mangareader.ui.smallPadding
import com.blanktheevil.mangareader.ui.smallPaddingVertical
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun TagsSelector(
    modifier: Modifier = Modifier,
    tags: List<Tag>,
    initialIncludedTags: List<Tag> = emptyList(),
    initialExcludedTags: List<Tag> = emptyList(),
    initialTagModes: Pair<TagsMode, TagsMode> = Pair(TagsMode.AND, TagsMode.OR),
    onTagStateChanged: (includedTags: List<Tag>, excludedTags: List<Tag>) -> Unit = { _, _ -> },
    onTagModeChanged: (includedTagMode: TagsMode, excludedTagsMode: TagsMode) -> Unit = { _, _ -> },
) {
    var expanded by remember { mutableStateOf(false) }
    var focused by remember { mutableStateOf(false) }
    val tagMap = tags
        .map { it.group }
        .associateWith {
            tags.filter { f -> f.group == it }
        }
    var includedTags by remember { mutableStateOf(initialIncludedTags) }
    var excludedTags by remember { mutableStateOf(initialExcludedTags) }
    val categories = tagMap.entries.toList()

    val text = listOf(
        includedTags.joinToString(", ") { "+${it.name}" },
        excludedTags.joinToString(", ") { "-${it.name}" },
    ).filterNot { it.isEmpty() }.joinToString(", ")

    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 0f else -90f,
        label = ""
    )

    val scope = rememberCoroutineScope()

    fun handleTagClicked(tag: Tag) {
        when (tag) {
            in includedTags -> {
                includedTags -= tag
                excludedTags += tag
            }

            in excludedTags -> {
                excludedTags -= tag
            }

            else -> {
                includedTags += tag
            }
        }
    }

    LaunchedEffect(key1 = includedTags, key2 = excludedTags) {
        onTagStateChanged(
            includedTags,
            excludedTags
        )
    }

    Column(modifier) {
        OutlinedTextField(
            readOnly = true,
            modifier = Modifier.onFocusChanged {
                expanded = it.isFocused || it.hasFocus
                focused = it.isFocused
            },
            label = {
                Text("Tags", style = MaterialTheme.typography.labelMedium)
            },
            value = text,
            onValueChange = {},
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = {
                    scope.launch { expanded = !expanded }
                }) {
                    // this will change to an x when a value is present
                    Icon(
                        modifier = Modifier.rotate(arrowRotation),
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            },
        )

        DropdownMenu(
            modifier = Modifier
                .padding(smallDp),
            expanded = expanded,
            onDismissRequest = {
                scope.launch {
                    expanded = false
                }
            },
            properties = PopupProperties(
                focusable = false
            )
        ) {
            DropDownMenuContent(
                categories = categories,
                includedTags = includedTags,
                excludedTags = excludedTags,
                initialTagModes = initialTagModes,
                handleTagClicked = ::handleTagClicked,
                onTagModeChanged = onTagModeChanged,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun DropDownMenuContent(
    categories: List<Map.Entry<String?, List<Tag>>>,
    includedTags: List<Tag>,
    excludedTags: List<Tag>,
    initialTagModes: Pair<TagsMode, TagsMode>,
    handleTagClicked: (Tag) -> Unit,
    onTagModeChanged: (TagsMode, TagsMode) -> Unit
) = Column(
    Modifier.height(LocalConfiguration.current.screenHeightDp.dp.div(1.75f))
) {
    val locale = LocalContext.current.resources.configuration.locales[0]
    val tagCta = stringResource(id = R.string.search_screen_tag_cta)

    var includedTagMode by remember { mutableStateOf(initialTagModes.first) }
    var excludedTagMode by remember { mutableStateOf(initialTagModes.second) }


    fun String.cap(locale: Locale): String {
        return this.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                locale
            ) else it.toString()
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(mediumDp),
            imageVector = Icons.Outlined.Warning,
            contentDescription = null
        )

        SpacerSmall()

        Text(
            text = tagCta,
            style = MaterialTheme.typography.labelMedium
        )
    }

    SpacerSmall()

    Column(
        Modifier
            .smallPaddingVertical()
            .verticalScroll(rememberScrollState())
            .weight(1f, false)

    ) {
        categories.filter { it.value.isNotEmpty() }.forEach { (k, v) ->
            Text(text = k?.cap(locale) ?: "Other")

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(smallDp)
            ) {
                v.forEach { c ->
                    val color = when (c) {
                        in includedTags -> {
                            MaterialTheme.colorScheme.primary
                        }

                        in excludedTags -> {
                            MaterialTheme.colorScheme.error
                        }

                        else -> {
                            MaterialTheme.colorScheme.outline
                        }
                    }

                    val icon = @Composable {
                        when (c) {
                            in includedTags -> {
                                Icon(
                                    modifier = Modifier.size(mediumDp),
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = null,
                                    tint = color,
                                )
                            }

                            in excludedTags -> {
                                Icon(
                                    modifier = Modifier.size(mediumDp),
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = null,
                                    tint = color,
                                )
                            }

                            else -> {
                                null
                            }
                        }
                    }


                    FilterChip(
                        leadingIcon = { icon() },
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = color
                        ),
                        selected = false,
                        onClick = { handleTagClicked(c) },
                        label = {
                            Text(
                                color = color,
                                text = c.name
                            )
                        })

                }
            }

            SpacerSmall()
        }
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Inclusion Mode")
            SegmentedButton(
                initialSelectedIndex = initialTagModes.first.ordinal,
                options = listOf("AND", "OR"), onSelected = {
                    includedTagMode = TagsMode.entries[it]
                    onTagModeChanged(
                        includedTagMode,
                        excludedTagMode,
                    )
                }
            )
        }

        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Exclusion Mode")
            SegmentedButton(
                initialSelectedIndex = initialTagModes.second.ordinal,
                options = listOf("AND", "OR"), onSelected = {
                    excludedTagMode = TagsMode.entries[it]
                    onTagModeChanged(
                        includedTagMode,
                        excludedTagMode,
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun PreviewTagSelectorContent() {
    val categories = StubData.TAGS
        .map { it.group }
        .associateWith {
            StubData.TAGS.filter { f -> f.group == it }
        }.entries.toList()

    Surface(Modifier.fillMaxSize()) {
        DefaultPreview {
            Column(Modifier.smallPadding()) {
                DropDownMenuContent(
                    categories = categories,
                    includedTags = listOf(StubData.TAGS[3]),
                    excludedTags = listOf(StubData.TAGS[1]),
                    handleTagClicked = { _ -> },
                    onTagModeChanged = { _, _ -> },
                    initialTagModes = Pair(TagsMode.AND, TagsMode.OR),
                )
            }
        }
    }
}