package com.blanktheevil.mangareader.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blanktheevil.mangareader.PreviewDataFactory
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.ui.components.ChapterFeed
import com.blanktheevil.mangareader.ui.components.MangaList
import com.blanktheevil.mangareader.ui.components.MangaSearchBar
import com.blanktheevil.mangareader.ui.components.MangaShelf
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    navigateToLogin: () -> Unit,
    navigateToMangaDetail: (id: String) -> Unit,
    navigateToReader: (String, String) -> Unit,
) {
    val context = LocalContext.current
    val uiState by homeViewModel.uiState.collectAsState()
    val textInput by homeViewModel.textInput.collectAsState()
    
    LaunchedEffect(Unit) {
        homeViewModel.initViewModel(context = context)
    }

    LaunchedEffect(textInput) {
        if (textInput.isNotEmpty()) {
            homeViewModel.searchManga(textInput)
        }
    }

    HomeScreenLayout(
        followedMangaList = uiState.followedMangaList,
        followedMangaLoading = uiState.followedMangaLoading,
        chapterFeedChapters = uiState.chapterFeedChapters,
        chapterFeedManga = uiState.chapterFeedManga,
        readChapterIds = uiState.readChapterIds,
        searchText = uiState.searchText,
        searchMangaList = uiState.searchMangaList,
        onTextChanged = homeViewModel::onTextChanged,
        logout = homeViewModel::logout,
        navigateToLogin = navigateToLogin,
        navigateToMangaDetail = navigateToMangaDetail,
        navigateToReader = navigateToReader,
    )
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
    logout: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToMangaDetail: (String) -> Unit,
    navigateToReader: (String, String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(48.dp)
    ) {
        item {
            Button(
                modifier = Modifier.padding(top = 8.dp),
                onClick = {
                    logout()
                    navigateToLogin()
                }
            ) {
                Text("Logout")
            }
        }

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
                loading = followedMangaLoading
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
            logout = { /*TODO*/ },
            navigateToLogin = { /*TODO*/ },
            navigateToMangaDetail = {},
            navigateToReader = {_,_->}
        )
    }
}