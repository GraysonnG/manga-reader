package com.blanktheevil.mangareader.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.ui.components.ChapterFeed
import com.blanktheevil.mangareader.ui.components.MangaShelf
import com.blanktheevil.mangareader.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    navigateToLogin: () -> Unit,
    navigateToMangaDetail: (id: String) -> Unit,
    navigateToReader: (String) -> Unit,
) {
    val context = LocalContext.current
    val uiState by homeViewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        homeViewModel.initViewModel(context = context)
    }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(48.dp)
    ) {
        item {
            Button(
                modifier = Modifier.padding(top = 8.dp),
                onClick = {
                    homeViewModel.logout()
                    navigateToLogin()
                }
            ) {
                Text("Logout")
            }
        }

        item {
            MangaShelf(
                title = stringResource(id = R.string.home_page_drawer_follows),
                list = uiState.followedMangaList,
                onCardClicked = navigateToMangaDetail,
                loading = uiState.followedMangaLoading
            )

        }

        item {
            ChapterFeed(
                title = "Recently Updated",
                chapterList = uiState.chapterFeedChapters,
                mangaList = uiState.chapterFeedManga,
                navigateToReader = navigateToReader,
            )
        }
    }
}