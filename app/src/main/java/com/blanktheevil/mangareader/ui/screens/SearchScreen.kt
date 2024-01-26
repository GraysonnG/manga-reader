package com.blanktheevil.mangareader.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.text.isDigitsOnly
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.Author
import com.blanktheevil.mangareader.data.MangaList
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.Tag
import com.blanktheevil.mangareader.data.TagsMode
import com.blanktheevil.mangareader.data.dto.TagList
import com.blanktheevil.mangareader.data.success
import com.blanktheevil.mangareader.ui.CONTENT_RATINGS_MAP
import com.blanktheevil.mangareader.ui.DEMOGRAPHICS_MAP
import com.blanktheevil.mangareader.ui.SORT_NAMES
import com.blanktheevil.mangareader.ui.SpacerLarge
import com.blanktheevil.mangareader.ui.SpacerMedium
import com.blanktheevil.mangareader.ui.components.MangaCard
import com.blanktheevil.mangareader.ui.components.MangaReaderTopAppBarState
import com.blanktheevil.mangareader.ui.components.SearchSelector
import com.blanktheevil.mangareader.ui.components.TagsSelector
import com.blanktheevil.mangareader.ui.components.TextSelector
import com.blanktheevil.mangareader.ui.smallDp
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
        FilterContent(
            uiState = uiState,
            filterState = filterState,
            tags = tags,
            mangaList = mangaList,
            setFilterVisible = viewModel::setFilterVisible,
            setFilterTitle = viewModel::setFilterTitle,
            setFilterTags = viewModel::setFilterTags,
            setFilterTagModes = viewModel::setFilterTagModes,
            setFilterAuthors = viewModel::setFilterAuthors,
            setFilterArtists = viewModel::setFilterArtists,
            setFilterContentRating = viewModel::setFilterContentRatings,
            setFilterDemographics = viewModel::setFilterDemographics,
            setFilterSortBy = viewModel::setFilterSortBy,
            setFilterStatus = viewModel::setFilterStatus,
            setFilterYear = viewModel::setFilterYear,
            getAuthorList = viewModel::getAuthorList,
            submit = viewModel::submit,
            resetFilters = viewModel::resetFilters,
        )
    }
}

@Composable
private fun FilterContent(
    uiState: SearchScreenViewModel.State,
    filterState: SearchScreenViewModel.FilterState,
    tags: TagList,
    mangaList: MangaList,
    setFilterTitle: (String) -> Unit,
    setFilterTags: (List<Tag>, List<Tag>) -> Unit,
    setFilterVisible: (Boolean) -> Unit,
    setFilterTagModes: (TagsMode, TagsMode) -> Unit,
    setFilterAuthors: (List<Author>) -> Unit,
    setFilterArtists: (List<Author>) -> Unit,
    setFilterDemographics: (String) -> Unit,
    setFilterContentRating: (String) -> Unit,
    setFilterYear: (Int?) -> Unit,
    setFilterSortBy: (String) -> Unit,
    setFilterStatus: (String) -> Unit,
    getAuthorList: suspend (String) -> Result<List<Author>>,
    submit: () -> Unit,
    resetFilters: () -> Unit,
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

    Box(
        Modifier
            .xSmallPaddingHorizontal()
    ) {
        Column {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(smallDp),
                horizontalArrangement = Arrangement.spacedBy(smallDp),
            ) {
                gridItem(span = 2) {
                    Column {
                        SpacerLarge()

                        Text(
                            text = "Advanced Search",
                            style = MaterialTheme.typography.headlineMedium
                        )

                        SpacerMedium()
                    }
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
                    AdvancedFilters(
                        filterState = filterState,
                        tags = tags,
                        enterTransition = enterTransition,
                        exitTransition = exitTransition,
                        setFilterTags = setFilterTags,
                        setFilterTagModes = setFilterTagModes,
                        setFilterAuthors = setFilterAuthors,
                        setFilterArtists = setFilterArtists,
                        setFilterDemographics = setFilterDemographics,
                        setFilterContentRating = setFilterContentRating,
                        setFilterYear = setFilterYear,
                        setFilterSortBy = setFilterSortBy,
                        setFilterStatus = setFilterStatus,
                        getAuthorList = getAuthorList
                    )
                }
                gridItem(span = 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(smallDp),
                    ) {
                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            enabled = filterState.isModified(),
                            onClick = resetFilters
                        ) {
                            Text("Reset Filters")
                        }

                        Button(
                            onClick = {
                                submit()
                                setFilterVisible(false)
                            }
                        ) {
                            Icon(Icons.Rounded.Search, contentDescription = null)
                            Text("Search")
                        }
                    }
                }
                if (uiState.loading) {
                    gridItem(span = 2) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    mangaList.forEach { manga ->
                        gridItem {
                            MangaCard(
                                manga = manga
                            )
                        }
                    }
                }
                gridItem(span = 2) { Spacer(Modifier) }
            }
        }
    }
}

@Composable
private fun AdvancedFilters(
    filterState: SearchScreenViewModel.FilterState,
    tags: List<Tag>,
    enterTransition: EnterTransition,
    exitTransition: ExitTransition,
    setFilterTags: (List<Tag>, List<Tag>) -> Unit,
    setFilterTagModes: (TagsMode, TagsMode) -> Unit,
    setFilterAuthors: (List<Author>) -> Unit,
    setFilterArtists: (List<Author>) -> Unit,
    setFilterDemographics: (String) -> Unit,
    setFilterContentRating: (String) -> Unit,
    setFilterYear: (Int?) -> Unit,
    setFilterSortBy: (String) -> Unit,
    setFilterStatus: (String) -> Unit,
    getAuthorList: suspend (String) -> Result<List<Author>>,
) {
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
                TextSelector(
                    modifier = Modifier.weight(1f),
                    placeholder = "Sort By",
                    valueMap = SORT_NAMES,
                    selectedValues = listOf(
                        filterState.order
                    ),
                    onValueSelected = setFilterSortBy
                )
                TextSelector(
                    modifier = Modifier.weight(1f),
                    placeholder = "Status",
                    valueMap = emptyMap(),
                    selectedValues = filterState.status.orEmpty(),
                    onValueSelected = setFilterStatus
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(smallDp)
            ) {
                TagsSelector(
                    modifier = Modifier.weight(1f),
                    tags = tags,
                    initialIncludedTags = tags.filter {
                        it in filterState.includedTags.orEmpty()
                    },
                    initialExcludedTags = tags.filter {
                        it in filterState.excludedTags.orEmpty()
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
                    valueMap = CONTENT_RATINGS_MAP,
                    selectedValues = filterState.contentRating,
                    onValueSelected = setFilterContentRating
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(smallDp)
            ) {
                TextSelector(
                    modifier = Modifier.weight(1f),
                    placeholder = "Demographic",
                    valueMap = DEMOGRAPHICS_MAP,
                    selectedValues = filterState.publicationDemographic
                        ?: emptyList(),
                    onValueSelected = setFilterDemographics
                )

                SearchSelector(
                    modifier = Modifier.weight(1f),
                    initialSelections = filterState.authors ?: emptyList(),
                    placeholder = "Authors",
                    getData = getAuthorList,
                    onValueChange = setFilterAuthors
                ) {
                    Text(it.name)
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(smallDp)
            ) {
                SearchSelector(
                    modifier = Modifier.weight(1f),
                    initialSelections = filterState.artists ?: emptyList(),
                    placeholder = "Artists",
                    getData = getAuthorList,
                    onValueChange = setFilterArtists
                ) {
                    Text(it.name)
                }
                Column(Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = filterState.year.orEmpty(),
                        onValueChange = {
                            if (it.isDigitsOnly()) {
                                setFilterYear(
                                    it.toIntOrNull()
                                )
                            }
                        },
                        label = {
                            Text(
                                text = "Year",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    )
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

@Composable
@Preview
private fun PreviewAdvancedFilters() {
    DefaultPreview {
        Surface {
            AdvancedFilters(
                filterState = SearchScreenViewModel.FilterState(
                    visible = true,
                ),
                tags = StubData.TAGS,
                enterTransition = EnterTransition.None,
                exitTransition = ExitTransition.None,
                setFilterTags = { _, _ -> },
                setFilterTagModes = { _, _ -> },
                setFilterAuthors = {},
                setFilterArtists = {},
                setFilterDemographics = {},
                setFilterContentRating = {},
                setFilterYear = {},
                setFilterSortBy = {},
                setFilterStatus = {},
                getAuthorList = { success(emptyList<Author>()) }
            )
        }
    }
}