package com.blanktheevil.mangareader.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.PreviewDataFactory
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.domain.ChapterFeedState
import com.blanktheevil.mangareader.ui.components.ChapterFeed
import com.blanktheevil.mangareader.ui.theme.MangaReaderDefaults
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.viewmodels.UpdatesScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatesScreen(
    viewModel: UpdatesScreenViewModel = viewModel(),
    setTopAppBar: (topAppBar: @Composable () -> Unit) -> Unit,
    navigateToReader: (String) -> Unit,
    navigateToMangaDetail: (String) -> Unit,
    popBackStack: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val chapterFeed by viewModel.chapterFeed()
    val followIcon = painterResource(id = R.drawable.round_bookmark_border_24)

    OnMount {
        viewModel.initViewModel(context)
    }

    setTopAppBar {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(painter = followIcon, contentDescription = null)
                    Text(text = stringResource(id = R.string.updates_title))
                }
            },
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

    Box(modifier = Modifier.fillMaxSize()) {
        UpdatesScreenLayout(
            uiState = uiState,
            chapterFeedState = chapterFeed,
            loadNextPage = viewModel::loadNextPage,
            loadPreviousPage = viewModel::loadPreviousPage,
            navigateToReader = navigateToReader,
            navigateToMangaDetail = navigateToMangaDetail,
        )
    }
}

@Composable
private fun UpdatesScreenLayout(
    uiState: UpdatesScreenViewModel.UpdatesScreenState,
    chapterFeedState: ChapterFeedState,
    loadNextPage: () -> Unit,
    loadPreviousPage: () -> Unit,
    navigateToReader: (String) -> Unit,
    navigateToMangaDetail: (String) -> Unit,
    isPreview: Boolean = false,
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
            isPreview = isPreview,
        )

        Spacer(modifier = Modifier.weight(1f, fill = true))

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
    val leftChevron = painterResource(id = R.drawable.round_chevron_left_24)
    val rightChevron = painterResource(id = R.drawable.round_chevron_right_24)


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(onClick = loadPreviousPage, enabled = currentPage > 0) {
            Icon(painter = leftChevron, contentDescription = null)
            Text("Prev")
        }

        Text(
            text = "${currentPage + 1} / ${maxPage + 1}",
            modifier = Modifier.weight(1f, fill = true),
            textAlign = TextAlign.Center,
            maxLines = 1,
        )

        TextButton(onClick = loadNextPage, enabled = currentPage < maxPage) {
            Text("Next")
            Icon(painter = rightChevron, contentDescription = null)
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewScreenDarkShort() {
    MangaReaderTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            UpdatesScreenLayout(
                uiState = UpdatesScreenViewModel.UpdatesScreenState(
                    maxPage = 98,
                ),
                chapterFeedState = ChapterFeedState(
                    chapterList = PreviewDataFactory.CHAPTER_LIST,
                    mangaList = PreviewDataFactory.MANGA_LIST.take(2),
                    loading = false
                ),
                loadNextPage = {},
                loadPreviousPage = {},
                navigateToReader = { _ -> },
                navigateToMangaDetail = {},
                isPreview = true,
            )
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewScreenDarkLong() {
    MangaReaderTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            UpdatesScreenLayout(
                uiState = UpdatesScreenViewModel.UpdatesScreenState(
                    maxPage = 98,
                ),
                chapterFeedState = ChapterFeedState(
                    chapterList = PreviewDataFactory.CHAPTER_LIST,
                    mangaList = PreviewDataFactory.MANGA_LIST,
                    loading = false
                ),
                loadNextPage = {},
                loadPreviousPage = {},
                navigateToReader = {},
                navigateToMangaDetail = {},
                isPreview = true,
            )
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewScreenDarkLoading() {
    MangaReaderTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            UpdatesScreenLayout(
                uiState = UpdatesScreenViewModel.UpdatesScreenState(
                    maxPage = 98,
                ),
                chapterFeedState = ChapterFeedState(
                    chapterList = PreviewDataFactory.CHAPTER_LIST,
                    mangaList = PreviewDataFactory.MANGA_LIST.take(2),
                    loading = true
                ),
                loadNextPage = {},
                loadPreviousPage = {},
                navigateToReader = {},
                navigateToMangaDetail = {},
                isPreview = true,
            )
        }
    }
}