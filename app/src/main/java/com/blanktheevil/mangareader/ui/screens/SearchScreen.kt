package com.blanktheevil.mangareader.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.MangaList
import com.blanktheevil.mangareader.data.Tag
import com.blanktheevil.mangareader.data.TagsMode
import com.blanktheevil.mangareader.data.dto.TagList
import com.blanktheevil.mangareader.ui.SpacerMedium
import com.blanktheevil.mangareader.ui.components.MangaCard
import com.blanktheevil.mangareader.ui.components.MangaReaderTopAppBarState
import com.blanktheevil.mangareader.ui.components.TagsSelector
import com.blanktheevil.mangareader.ui.components.TextSelector
import com.blanktheevil.mangareader.ui.smallDp
import com.blanktheevil.mangareader.ui.smallPaddingVertical
import com.blanktheevil.mangareader.ui.xSmallDp
import com.blanktheevil.mangareader.ui.xSmallPaddingHorizontal
import com.blanktheevil.mangareader.viewmodels.SearchScreenViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    viewModel: SearchScreenViewModel = koinViewModel(),
    setTopAppBarState: (MangaReaderTopAppBarState) -> Unit,
) {
    val title = stringResource(id = R.string.search_screen_title)
    val uiState by viewModel.uiState.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val mangaList by viewModel.mangaList.collectAsState()


    setTopAppBarState(
        MangaReaderTopAppBarState(
            title = title,
            show = false,
        )
    )


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchScreenLayout(
            uiState = uiState,
            filterState = filterState,
            tags = tags,
            mangaList = mangaList,
            setFilterVisible = viewModel::setFilterVisible,
            setFilterTitle = viewModel::setFilterTitle,
            setFilterTags = viewModel::setFilterTags,
            setFilterTagModes = viewModel::setFilterTagModes,
        )
    }
}

@Composable
private fun SearchScreenLayout(
    uiState: SearchScreenViewModel.State,
    filterState: SearchScreenViewModel.FilterState,
    tags: TagList,
    mangaList: MangaList,
    setFilterVisible: (Boolean) -> Unit,
    setFilterTitle: (String) -> Unit,
    setFilterTags: (List<Tag>, List<Tag>) -> Unit,
    setFilterTagModes: (TagsMode, TagsMode) -> Unit,
) {
    FilterContent(
        filterState = filterState,
        tags = tags,
        mangaList = mangaList,
        setFilterTitle = setFilterTitle,
        setFilterTags = setFilterTags,
        setFilterVisible = setFilterVisible,
        setFilterTagModes = setFilterTagModes,
    )
}

@Composable
private fun FilterContent(
    filterState: SearchScreenViewModel.FilterState,
    tags: TagList,
    mangaList: MangaList,
    setFilterTitle: (String) -> Unit,
    setFilterTags: (List<Tag>, List<Tag>) -> Unit,
    setFilterVisible: (Boolean) -> Unit,
    setFilterTagModes: (TagsMode, TagsMode) -> Unit,
) {
    var contentRatingSelections by remember {
        mutableStateOf(emptyList<String>())
    }

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

    Box(
        Modifier
            .smallPaddingVertical()
            .xSmallPaddingHorizontal()
    ) {
        Column {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(smallDp),
                horizontalArrangement = Arrangement.spacedBy(smallDp),
            ) {
                gridItem(span = 2) {
                    Text(
                        text = "Advanced Search",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    SpacerMedium()
                }
                gridItem(span = 2) {
                    OutlinedTextField(
                        leadingIcon = {
                            Icon(Icons.Rounded.Search, contentDescription = null)
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { setFilterVisible(!filterState.visible) }
                            ) {
                                Icon(painterResource(id = R.drawable.twotone_filter_alt_24), null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(
                                style = MaterialTheme.typography.labelMedium,
                                text = "Search..."
                            )
                        },
                        value = filterState.title,
                        onValueChange = setFilterTitle,
                        singleLine = true,
                    )
                }
                gridItem(span = 2) {
                    AnimatedVisibility(
                        visible = filterState.visible,
                        enter = enterTransition,
                        exit = exitTransition,
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(xSmallDp),
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(smallDp)
                            ) {
                                TagsSelector(
                                    modifier = Modifier.weight(1f),
                                    tags = tags,
                                    initialIncludedTags = tags.filter {
                                        it.id in filterState.includedTagIds.orEmpty()
                                    },
                                    initialExcludedTags = tags.filter {
                                        it.id in filterState.excludedTagIds.orEmpty()
                                    },
                                    onTagStateChanged = setFilterTags,
                                    onTagModeChanged = setFilterTagModes,
                                    initialTagModes = Pair(
                                        filterState.includedTagsMode,
                                        filterState.excludedTagsMode,
                                    )
                                )
                                TextSelector(
                                    modifier = Modifier.weight(1f),
                                    placeholder = "Content Rating",
                                    valueMap = mapOf(
                                        "safe" to "Safe",
                                        "suggestive" to "Suggestive",
                                        "ero" to "Erotica"
                                    ),
                                    selectedValues = contentRatingSelections,
                                    onValueSelected = { selected ->
                                        if (selected in contentRatingSelections) {
                                            contentRatingSelections =
                                                contentRatingSelections.filterNot { it == selected }
                                        } else {
                                            contentRatingSelections += selected
                                        }
                                    }
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(smallDp)
                            ) {
                                TextSelector(
                                    modifier = Modifier.weight(1f),
                                    placeholder = "Demographic",
                                    valueMap = emptyMap(),
                                    selectedValues = emptyList(),
                                    onValueSelected = {}
                                )
                                TextSelector(
                                    modifier = Modifier.weight(1f),
                                    placeholder = "Authors",
                                    valueMap = emptyMap(),
                                    selectedValues = emptyList(),
                                    onValueSelected = {}
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(smallDp)
                            ) {
                                TextSelector(
                                    modifier = Modifier.weight(1f),
                                    placeholder = "Artists",
                                    valueMap = emptyMap(),
                                    selectedValues = emptyList(),
                                    onValueSelected = {}
                                )
                                TextSelector(
                                    modifier = Modifier.weight(1f),
                                    placeholder = "Year",
                                    valueMap = emptyMap(),
                                    selectedValues = emptyList(),
                                    onValueSelected = {}
                                )
                            }
                        }
                    }
                }
                gridItem(span = 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(smallDp),
                    ) {
                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            enabled = false,
                            onClick = { /*TODO*/ }) {
                            Text("Reset Filters")
                        }

                        Button(
                            onClick = {
                                setFilterVisible(false)
                            }) {
                            Icon(Icons.Rounded.Search, contentDescription = null)
                            Text("Search")
                        }
                    }
                }
                mangaList.forEach { manga ->
                    gridItem {
                        MangaCard(
                            manga = manga
                        )
                    }
                }
            }
        }
    }
}

private fun LazyGridScope.gridItem(
    span: Int = 1,
    content: @Composable LazyGridItemScope.() -> Unit,
) =
    item(
        span = { GridItemSpan(span) },
        content = content,
    )

@Composable
@Preview
private fun Preview() {
    DefaultPreview {
        Surface {
            SearchScreen(setTopAppBarState = {})
        }
    }
}