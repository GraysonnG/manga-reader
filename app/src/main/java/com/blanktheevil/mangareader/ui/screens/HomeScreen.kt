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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.PreviewDataFactory
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.ui.components.ChapterFeed
import com.blanktheevil.mangareader.ui.components.HomeUserMenu
import com.blanktheevil.mangareader.ui.components.MangaSearchBar
import com.blanktheevil.mangareader.ui.components.MangaShelf
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    navigateToLogin: () -> Unit,
    navigateToMangaDetail: (id: String) -> Unit,
    navigateToReader: (String, String) -> Unit,
    navigateToLibraryScreen: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by homeViewModel.uiState.collectAsState()
    val textInput by homeViewModel.textInput.collectAsState()

    OnMount {
        homeViewModel.initViewModel(context = context)
    }

    LaunchedEffect(textInput) {
        if (textInput.isNotEmpty()) {
            homeViewModel.searchManga(textInput)
        }
    }

    Scaffold(
        topBar = { TopAppBar(
            title = { Text(text = "Home") },
            actions = {
                HomeUserMenu(
                    onLogoutClicked = {
                        homeViewModel.logout()
                        navigateToLogin()
                    }
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                scrolledContainerColor = MaterialTheme.colorScheme.primary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = Color.Unspecified,
            ),
        ) }
    ) {
        HomeScreenLayout(
            modifier = Modifier.padding(paddingValues = it),
            followedMangaList = uiState.followedMangaList,
            followedMangaLoading = uiState.followedMangaLoading,
            chapterFeedChapters = uiState.chapterFeedChapters,
            chapterFeedManga = uiState.chapterFeedManga,
            readChapterIds = uiState.readChapterIds,
            searchText = uiState.searchText,
            searchMangaList = uiState.searchMangaList,
            onTextChanged = homeViewModel::onTextChanged,
            navigateToMangaDetail = navigateToMangaDetail,
            navigateToReader = navigateToReader,
            navigateToLibraryScreen = navigateToLibraryScreen,
        )
    }
}

@Composable
private fun HomeScreenLayout(
    followedMangaList: List<MangaDto>,
    followedMangaLoading: Boolean,
    chapterFeedChapters: List<ChapterDto>,
    chapterFeedManga: List<MangaDto>,
    readChapterIds: List<String>,
    searchText: String,
    searchMangaList: List<MangaDto>,
    onTextChanged: (String) -> Unit,
    navigateToMangaDetail: (String) -> Unit,
    navigateToReader: (String, String) -> Unit,
    navigateToLibraryScreen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 8.dp),
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
            MangaShelf(
                title = stringResource(id = R.string.home_page_drawer_follows),
                list = followedMangaList,
                onCardClicked = navigateToMangaDetail,
                loading = followedMangaLoading,
                navigateToLibraryScreen = navigateToLibraryScreen,
            )

        }

        item {
            ChapterFeed(
                title = "Recently Updated",
                chapterList = chapterFeedChapters,
                mangaList = chapterFeedManga,
                navigateToReader = navigateToReader,
                readChapterIds = readChapterIds,
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

@Preview(heightDp = 2000, showBackground = true)
@Composable
private fun Preview1() {
    MangaReaderTheme {
        HomeScreenLayout(
            followedMangaList = PreviewDataFactory.MANGA_LIST,
            followedMangaLoading = false,
            chapterFeedChapters = PreviewDataFactory.CHAPTER_LIST,
            chapterFeedManga = PreviewDataFactory.MANGA_LIST,
            readChapterIds = emptyList(),
            searchText = "",
            searchMangaList = PreviewDataFactory.MANGA_LIST,
            onTextChanged = {},
            navigateToMangaDetail = {},
            navigateToReader = { _, _ -> },
            navigateToLibraryScreen = {}
        )
    }
}