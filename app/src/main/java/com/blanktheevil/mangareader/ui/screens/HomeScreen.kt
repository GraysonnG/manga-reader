package com.blanktheevil.mangareader.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.PreviewDataFactory
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.domain.ChapterFeedDataStore
import com.blanktheevil.mangareader.domain.ChapterFeedState
import com.blanktheevil.mangareader.domain.FollowedMangaDataStore
import com.blanktheevil.mangareader.domain.FollowedMangaState
import com.blanktheevil.mangareader.domain.PopularFeedDataStore
import com.blanktheevil.mangareader.domain.PopularFeedState
import com.blanktheevil.mangareader.ui.components.ChapterFeed
import com.blanktheevil.mangareader.ui.components.HomeUserMenu
import com.blanktheevil.mangareader.ui.components.MangaSearchBar
import com.blanktheevil.mangareader.ui.components.MangaShelf
import com.blanktheevil.mangareader.ui.theme.MangaReaderDefaults
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    setTopAppBar: (@Composable () -> Unit) -> Unit,
    navigateToLogin: () -> Unit,
    navigateToMangaDetail: (id: String) -> Unit,
    navigateToReader: (String, String) -> Unit,
    navigateToLibraryScreen: (LibraryType) -> Unit,
) {
    val context = LocalContext.current
    val uiState by homeViewModel.uiState.collectAsState()
    val followedMangaState by homeViewModel.followedManga()
    val chapterFeedState by homeViewModel.chapterFeed()
    val popularFeedState by homeViewModel.popularFeed()
    val textInput by homeViewModel.textInput.collectAsState()

    setTopAppBar {
        TopAppBar(
            title = { Text(text = "Home") },
            actions = {
                HomeUserMenu(
                    onLogoutClicked = {
                        homeViewModel.logout()
                        navigateToLogin()
                    }
                )
            },
            colors = MangaReaderDefaults.topAppBarColors(),
        )
    }

    OnMount {
        homeViewModel.initViewModel(context = context)
    }

    LaunchedEffect(textInput) {
        if (textInput.isNotEmpty()) {
            homeViewModel.searchManga(textInput)
        }
    }
    
    popularFeedState.error?.let {
        Snackbar(
            action = if (!homeViewModel.popularFeed.hasRetried) {
                { Button(onClick = { homeViewModel.popularFeed.retry() }) {
                    Text(text = "Retry")
                } }
            } else {
                { Button(onClick = {  }) {
                    Text(text = "Ok")
                } }
            }
        ) {
            Column {
                Text(text = "Error: ${it.getErrorTitle()}")
            }
        }
    }

    HomeScreenLayout(
        followedMangaState = followedMangaState,
        chapterFeedState = chapterFeedState,
        popularFeedState = popularFeedState,
        searchText = uiState.searchText,
        searchMangaList = uiState.searchMangaList,
        onTextChanged = homeViewModel::onTextChanged,
        navigateToMangaDetail = navigateToMangaDetail,
        navigateToReader = navigateToReader,
        navigateToLibraryScreen = navigateToLibraryScreen,
    )
}

@Composable
private fun HomeScreenLayout(
    followedMangaState: FollowedMangaState,
    chapterFeedState: ChapterFeedState,
    popularFeedState: PopularFeedState,
    searchText: String,
    searchMangaList: List<MangaDto>,
    onTextChanged: (String) -> Unit,
    navigateToMangaDetail: (String) -> Unit,
    navigateToReader: (String, String) -> Unit,
    navigateToLibraryScreen: (LibraryType) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(48.dp)
    ) {
        item {
            MangaSearchBar(
                manga = searchMangaList,
                value = searchText,
                onValueChange = onTextChanged,
                navigateToMangaDetail = navigateToMangaDetail,
            )
        }

        item {
            ChapterFeed(
                title = stringResource(id = R.string.home_page_feed_recently_updated),
                chapterList = chapterFeedState.chapterList,
                mangaList = chapterFeedState.mangaList,
                navigateToReader = navigateToReader,
                navigateToMangaDetail = navigateToMangaDetail,
                readChapterIds = chapterFeedState.readChapters,
            )
        }

        item {
            MangaShelf(
                title = stringResource(id = R.string.home_page_drawer_follows),
                list = followedMangaState.list,
                onCardClicked = navigateToMangaDetail,
                loading = followedMangaState.loading,
                onTitleClicked = { navigateToLibraryScreen(LibraryType.FOLLOWS) },
            )
        }

        item {
            MangaShelf(
                title = stringResource(id = R.string.home_page_drawer_recently_popular),
                list = popularFeedState.mangaList,
                loading = popularFeedState.loading,
                onCardClicked = navigateToMangaDetail,
                onTitleClicked = { navigateToLibraryScreen(LibraryType.POPULAR) },
            )
        }
    }
}

@Composable
private fun HomeMenu(
    menuOpen: Boolean,
    userName: String,
    avatar: Painter,
    onDismissRequest: () -> Unit,
    onLogoutClicked: () -> Unit,
) {
    DropdownMenu(expanded = menuOpen, onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier
                    .clip(CircleShape)
                    .height(75.dp)
                    .width(75.dp),
                painter = avatar,
                contentDescription = ""
            )
            Text(text = userName, style = MaterialTheme.typography.titleMedium)
            Button(onClick = onLogoutClicked) {
                Text(text = "Logout")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewShort() {
    MangaReaderTheme {
        HomeScreenLayout(
            followedMangaState = FollowedMangaState(
                list = PreviewDataFactory.MANGA_LIST,
                loading = false,
            ),
            chapterFeedState = ChapterFeedState(
                chapterList = PreviewDataFactory.CHAPTER_LIST,
                mangaList = PreviewDataFactory.MANGA_LIST,
                readChapters = emptyList(),
            ),
            popularFeedState = PopularFeedState(
                mangaList = PreviewDataFactory.MANGA_LIST,
                loading = false,
            ),
            searchText = "",
            searchMangaList = emptyList(),
            onTextChanged = {},
            navigateToMangaDetail = {},
            navigateToReader = { _, _ -> },
            navigateToLibraryScreen = {},
        )
    }
}

@Preview(heightDp = 2000, showBackground = true)
@Composable
private fun Preview1() {
    MangaReaderTheme {
        HomeScreenLayout(
            followedMangaState = FollowedMangaState(
                list = PreviewDataFactory.MANGA_LIST,
                loading = false,
            ),
            chapterFeedState = ChapterFeedState(
                chapterList = PreviewDataFactory.CHAPTER_LIST,
                mangaList = PreviewDataFactory.MANGA_LIST,
                readChapters = emptyList(),
            ),
            popularFeedState = PopularFeedState(
                mangaList = PreviewDataFactory.MANGA_LIST,
                loading = false,
            ),
            searchText = "",
            searchMangaList = emptyList(),
            onTextChanged = {},
            navigateToMangaDetail = {},
            navigateToReader = { _, _ -> },
            navigateToLibraryScreen = {},
        )
    }
}