package com.blanktheevil.mangareader.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.domain.ChapterFeedState
import com.blanktheevil.mangareader.ui.PullToRefreshScreen
import com.blanktheevil.mangareader.ui.components.ChapterFeed
import com.blanktheevil.mangareader.ui.components.MangaReaderTopAppBarState
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.viewmodels.UpdatesScreenViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun UpdatesScreen(
    viewModel: UpdatesScreenViewModel = koinViewModel(),
    setTopAppBarState: (MangaReaderTopAppBarState) -> Unit,
    navigateToReader: (String) -> Unit,
    navigateToMangaDetail: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val chapterFeed by viewModel.chapterFeed()
    val titleString = stringResource(id = R.string.updates_title)
    val followIcon = painterResource(id = R.drawable.round_bookmark_border_24)

    OnMount {
        viewModel.initViewModel()
    }

    setTopAppBarState(
        MangaReaderTopAppBarState(
            title = titleString,
            titleIcon = followIcon,
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        UpdatesScreenLayout(
            uiState = uiState,
            chapterFeedState = chapterFeed,
            refresh = viewModel.chapterFeed::refresh,
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
    refresh: () -> Unit,
    loadNextPage: () -> Unit,
    loadPreviousPage: () -> Unit,
    navigateToReader: (String) -> Unit,
    navigateToMangaDetail: (String) -> Unit,
) {
    val scrollState = rememberScrollState()

    PullToRefreshScreen(
        onRefresh = refresh,
        content = @Composable {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ChapterFeed(
                    chapterFeedState = chapterFeedState,
                    navigateToReader = navigateToReader,
                    navigateToMangaDetail = navigateToMangaDetail,
                )

                Spacer(Modifier.weight(1f, fill = true))

                ScreenNavigationControls(
                    scrollState = scrollState,
                    currentPage = uiState.page,
                    maxPage = uiState.maxPage,
                    loadNextPage = loadNextPage,
                    loadPreviousPage = loadPreviousPage,
                )
            }
        }
    )
}

@Composable
private fun ScreenNavigationControls(
    scrollState: ScrollState,
    currentPage: Int,
    maxPage: Int,
    loadNextPage: () -> Unit,
    loadPreviousPage: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val leftChevron = painterResource(id = R.drawable.round_chevron_left_24)
    val rightChevron = painterResource(id = R.drawable.round_chevron_right_24)
    val onNextClick = remember {
        {
            coroutineScope.launch {
                loadNextPage()
                scrollState.scrollTo(0)
            }
            Unit
        }
    }
    val onPrevClick = remember {
        {
            coroutineScope.launch {
                loadPreviousPage()
                scrollState.scrollTo(0)
            }
            Unit
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(onClick = onPrevClick, enabled = currentPage > 0) {
            Icon(painter = leftChevron, contentDescription = null)
            Text("Prev")
        }

        Text(
            text = "${currentPage + 1} / ${maxPage + 1}",
            modifier = Modifier.weight(1f, fill = true),
            textAlign = TextAlign.Center,
            maxLines = 1,
        )

        TextButton(
            onClick = onNextClick, enabled = currentPage < maxPage
        ) {
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
                    loading = false
                ),
                loadNextPage = {},
                loadPreviousPage = {},
                navigateToReader = { _ -> },
                navigateToMangaDetail = {},
                refresh = {},
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
                    loading = false
                ),
                loadNextPage = {},
                loadPreviousPage = {},
                navigateToReader = {},
                navigateToMangaDetail = {},
                refresh = {},
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
                    loading = true
                ),
                loadNextPage = {},
                loadPreviousPage = {},
                navigateToReader = {},
                navigateToMangaDetail = {},
                refresh = {},
            )
        }
    }
}