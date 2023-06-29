package com.blanktheevil.mangareader.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.domain.ChapterFeedState
import com.blanktheevil.mangareader.ui.components.ChapterFeed
import com.blanktheevil.mangareader.ui.theme.MangaReaderDefaults
import com.blanktheevil.mangareader.viewmodels.UpdatesScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatesScreen(
    viewModel: UpdatesScreenViewModel = viewModel(),
    setTopAppBar: (topAppBar: @Composable () -> Unit) -> Unit,
    navigateToReader: (String, String) -> Unit,
    navigateToMangaDetail: (String) -> Unit,
    popBackStack: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val chapterFeed by viewModel.chapterFeed()

    OnMount {
        viewModel.initViewModel(context)
    }

    setTopAppBar {
        TopAppBar(
            title = { Text(text = "Follows Updates") },
            colors = MangaReaderDefaults.topAppBarColors(),
            navigationIcon = {
                IconButton(onClick = popBackStack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            },
        )
    }

    UpdatesScreenLayout(
        uiState = uiState,
        chapterFeedState = chapterFeed,
        loadNextPage = viewModel::loadNextPage,
        loadPreviousPage = viewModel::loadPreviousPage,
        navigateToReader = navigateToReader,
        navigateToMangaDetail = navigateToMangaDetail,
    )
}

@Composable
private fun UpdatesScreenLayout(
    uiState: UpdatesScreenViewModel.UpdatesScreenState,
    chapterFeedState: ChapterFeedState,
    loadNextPage: () -> Unit,
    loadPreviousPage: () -> Unit,
    navigateToReader: (String, String) -> Unit,
    navigateToMangaDetail: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ChapterFeed(
            modifier = Modifier
                .padding(vertical = 8.dp),
            title = null,
            unCapped = true,
            chapterList = chapterFeedState.chapterList,
            mangaList = chapterFeedState.mangaList,
            readChapterIds = chapterFeedState.readChapters,
            loading = chapterFeedState.loading,
            navigateToReader = navigateToReader,
            navigateToMangaDetail = navigateToMangaDetail,
        )

        ScreenNavigationControls(
            currentPage = uiState.page,
            maxPage = uiState.maxPage,
            loadNextPage = loadNextPage,
            loadPreviousPage = loadPreviousPage,
        )
    }
}

@Composable
private fun ScreenNavigationControls(
    currentPage: Int,
    maxPage: Int,
    loadNextPage: () -> Unit,
    loadPreviousPage: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = loadPreviousPage,
        ) {
            Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null, tint = Color.White)
        }

        Text(
            text = "${currentPage + 1} / ${maxPage + 1}",
            modifier = Modifier.weight(1f, fill = true),
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )

        IconButton(
            onClick = loadNextPage,
        ) {
            Icon(imageVector = Icons.Rounded.ArrowForward, contentDescription = null, tint = Color.White)
        }
    }
}