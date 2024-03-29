package com.blanktheevil.mangareader.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.text.isDigitsOnly
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.Author
import com.blanktheevil.mangareader.data.ContentRatings
import com.blanktheevil.mangareader.data.Demographics
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.Sort
import com.blanktheevil.mangareader.data.Status
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.Tag
import com.blanktheevil.mangareader.data.TagsMode
import com.blanktheevil.mangareader.data.dto.utils.MangaList
import com.blanktheevil.mangareader.data.dto.utils.TagList
import com.blanktheevil.mangareader.data.dto.utils.manga.toMangaList
import com.blanktheevil.mangareader.data.success
import com.blanktheevil.mangareader.ui.components.MangaCard
import com.blanktheevil.mangareader.ui.components.MangaCardShimmer
import com.blanktheevil.mangareader.ui.components.MangaReaderTextField
import com.blanktheevil.mangareader.ui.components.MangaReaderTopAppBarState
import com.blanktheevil.mangareader.ui.components.SearchSelector
import com.blanktheevil.mangareader.ui.components.TagsSelector
import com.blanktheevil.mangareader.ui.components.TextSelector
import com.blanktheevil.mangareader.ui.setTopAppBarState
import com.blanktheevil.mangareader.ui.smallDp
import com.blanktheevil.mangareader.ui.smallPaddingHorizontal
import com.blanktheevil.mangareader.ui.smallPaddingVertical
import com.blanktheevil.mangareader.ui.xSmallDp
import com.blanktheevil.mangareader.viewmodels.SearchScreenViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    viewModel: SearchScreenViewModel = koinViewModel(),
) {
    val title = stringResource(id = R.string.search_screen_title)
    val titleIcon = painterResource(id = R.drawable.twotone_search_24)
    val uiState by viewModel.uiState.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val mangaList by viewModel.mangaList.collectAsState()

    setTopAppBarState(
        MangaReaderTopAppBarState(
            title = title,
            titleIcon = titleIcon,
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
private fun SearchScreenLayout(
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
    val keyboardController = LocalSoftwareKeyboardController.current
    val enterTransition = remember {
        expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween()
        )
    }
    val exitTransition = remember {
        shrinkVertically(
            // Expand from the top.
            shrinkTowards = Alignment.Top,
            animationSpec = tween()
        )
    }

    Box(
        Modifier
            .smallPaddingHorizontal()
    ) {
        Column {
            AnimatedContent(
                modifier = Modifier.weight(1f),
                targetState = uiState.loading,
                transitionSpec = {
                    fadeIn(
                        animationSpec = tween(600)
                    )
                        .togetherWith(
                            fadeOut(
                                animationSpec = tween(600)
                            )
                        )
                },
                label = "content"
            ) {
                if (it) {
                    ShimmerLayout()
                } else {
                    MainLayout(
                        mangaList = mangaList
                    )
                }
            }

            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .smallPaddingVertical(),
                verticalArrangement = Arrangement.spacedBy(smallDp)
            ) {
                MangaReaderTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = filterState.title,
                    onValueChange = setFilterTitle,
                    trailingIcon = {
                        Row {
                            IconButton(
                                onClick = { setFilterVisible(!filterState.visible) }
                            ) {
                                Icon(
                                    painterResource(id = R.drawable.round_filter_list_24),
                                    null
                                )
                            }

                            IconButton(
                                onClick = {
                                    keyboardController?.hide()
                                    submit()
                                    setFilterVisible(false)
                                },
                                colors = IconButtonDefaults.filledIconButtonColors()
                            ) {
                                Icon(
                                    Icons.Rounded.Search,
                                    contentDescription = stringResource(
                                        id = R.string.search_screen_button_search
                                    )
                                )
                            }
                        }
                    },
                    placeholder = {
                        Text(
                            style = MaterialTheme.typography.labelMedium,
                            text = stringResource(id = R.string.search_screen_field_search)
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            submit()
                            setFilterVisible(false)
                            keyboardController?.hide()
                        }
                    )
                )

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
                    getAuthorList = getAuthorList,
                    resetFilters = resetFilters,
                )
            }
        }
    }
}

@Composable
private fun MainLayout(
    mangaList: MangaList,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(smallDp),
        horizontalArrangement = Arrangement.spacedBy(smallDp),
    ) {
        gridItem(span = 2) { }

        gridItems(
            mangaList,
            key = { it.id }
        ) { manga ->
            MangaCard(
                modifier = Modifier,
                manga = manga
            )
        }

        gridItem(span = 2) { Spacer(Modifier) }
    }
}

@Composable
private fun ShimmerLayout() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(smallDp),
        horizontalArrangement = Arrangement.spacedBy(smallDp),
    ) {
        gridItem(span = 2) { }

        gridItems(
            List(8) { 0 }
        ) {
            MangaCardShimmer()
        }

        gridItem(span = 2) { Spacer(Modifier) }
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
    resetFilters: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

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
                    placeholder = stringResource(id = R.string.search_screen_field_sort_by),
                    valueMap = Sort.toValueMap(),
                    selectedValues = listOf(
                        filterState.order
                    ),
                    onValueSelected = setFilterSortBy
                )
                TextSelector(
                    modifier = Modifier.weight(1f),
                    placeholder = stringResource(id = R.string.search_screen_field_status),
                    valueMap = Status.toValueMap(),
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
                    placeholder = stringResource(id = R.string.search_screen_field_content_rating),
                    valueMap = ContentRatings.toValueMap(),
                    selectedValues = filterState.contentRating,
                    onValueSelected = setFilterContentRating
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(smallDp)
            ) {
                TextSelector(
                    modifier = Modifier.weight(1f),
                    placeholder = stringResource(id = R.string.search_screen_field_demographic),
                    valueMap = Demographics.toValueMap(),
                    selectedValues = filterState.publicationDemographic
                        ?: emptyList(),
                    onValueSelected = setFilterDemographics
                )

                SearchSelector(
                    modifier = Modifier.weight(1f),
                    initialSelections = filterState.authors ?: emptyList(),
                    placeholder = stringResource(id = R.string.search_screen_field_authors),
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
                    placeholder = stringResource(id = R.string.search_screen_field_artists),
                    getData = getAuthorList,
                    onValueChange = setFilterArtists
                ) {
                    Text(it.name)
                }
                Column(Modifier.weight(1f)) {
                    MangaReaderTextField(
                        value = filterState.year.orEmpty(),
                        onValueChange = {
                            if (it.isDigitsOnly()) {
                                setFilterYear(
                                    it.toIntOrNull()
                                )
                            }
                        },
                        placeholder = {
                            Text(
                                text = stringResource(id = R.string.search_screen_field_year),
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Next)
                            }
                        )
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(smallDp),
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Button(
                    enabled = filterState.isModified(),
                    onClick = resetFilters
                ) {
                    Text(text = stringResource(id = R.string.search_screen_button_reset))
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

private fun <T> LazyGridScope.gridItems(
    items: List<T>,
    itemSpan: (T) -> Int = { 1 },
    key: ((item: T) -> Any)? = null,
    itemContent: @Composable (T) -> Unit
) {
    items(items = items, key = key) { item ->
        itemContent(item)
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun Preview() {
    DefaultPreview {
        Surface {
            SearchScreenLayout(
                uiState = SearchScreenViewModel.State(
                    mangaList = StubData.Data.MANGA_LIST.toMangaList(),
                    loading = false,
                ),
                filterState = SearchScreenViewModel.FilterState(),
                tags = StubData.Data.TAGS,
                mangaList = StubData.Data.MANGA_LIST.toMangaList(),
                getAuthorList = { success(emptyList()) },
                resetFilters = {},
                submit = {},
                setFilterArtists = {},
                setFilterAuthors = {},
                setFilterContentRating = {},
                setFilterDemographics = {},
                setFilterSortBy = {},
                setFilterStatus = {},
                setFilterTags = { _, _ -> },
                setFilterTagModes = { _, _ -> },
                setFilterTitle = {},
                setFilterVisible = {},
                setFilterYear = {},
            )
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun PreviewLoading() {
    DefaultPreview {
        Surface {
            SearchScreenLayout(
                uiState = SearchScreenViewModel.State(
                    mangaList = StubData.Data.MANGA_LIST.toMangaList(),
                    loading = true,
                ),
                filterState = SearchScreenViewModel.FilterState(),
                tags = StubData.Data.TAGS,
                mangaList = StubData.Data.MANGA_LIST.toMangaList(),
                getAuthorList = { success(emptyList()) },
                resetFilters = {},
                submit = {},
                setFilterArtists = {},
                setFilterAuthors = {},
                setFilterContentRating = {},
                setFilterDemographics = {},
                setFilterSortBy = {},
                setFilterStatus = {},
                setFilterTags = { _, _ -> },
                setFilterTagModes = { _, _ -> },
                setFilterTitle = {},
                setFilterVisible = {},
                setFilterYear = {},
            )
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun PreviewAdvancedFilters() {
    DefaultPreview {
        Surface {
            AdvancedFilters(
                filterState = SearchScreenViewModel.FilterState(
                    visible = true,
                ),
                tags = StubData.Data.TAGS,
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
                getAuthorList = { success(emptyList()) },
                resetFilters = {},
            )
        }
    }
}