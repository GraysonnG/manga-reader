package com.blanktheevil.mangareader.reader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.PreviewDataFactory
import com.blanktheevil.mangareader.di.appModule
import com.blanktheevil.mangareader.di.dataStoresModule
import com.blanktheevil.mangareader.reader.components.PageReaderV2
import com.blanktheevil.mangareader.reader.components.ReaderUI
import com.blanktheevil.mangareader.viewmodels.ReaderState
import com.blanktheevil.mangareader.viewmodels.ReaderType
import com.blanktheevil.mangareader.viewmodels.ReaderViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.core.context.startKoin

@Composable
fun ReaderScreenV2(
    chapterId: String,
    readerViewModel: ReaderViewModel = koinViewModel(),
) {
    val uiState by readerViewModel.uiState.collectAsState()
    var showDetail by remember { mutableStateOf(false) }

    OnMount {
        readerViewModel.initReader(chapterId = chapterId)
    }

    ReaderScreenLayout(
        modifier = Modifier.fillMaxSize(),
        readerState = uiState,
        showDetail = false,
        toggleShowDetail = { showDetail = !showDetail },
        nextButtonClicked = readerViewModel::nextButtonClicked,
        prevButtonClicked = readerViewModel::prevPage,
        goToNextChapter = readerViewModel::nextChapter,
        goToPrevChapter = readerViewModel::prevChapter,
    )
}

@Composable
private fun ReaderScreenLayout(
    modifier: Modifier = Modifier,
    readerState: ReaderState,
    showDetail: Boolean,
    toggleShowDetail: () -> Unit,
    nextButtonClicked: () -> Unit,
    prevButtonClicked: () -> Unit,
    goToNextChapter: () -> Unit,
    goToPrevChapter: () -> Unit,
) = Box(modifier = modifier) {
    PageReaderV2(
        currentPage = readerState.currentPage,
        maxPages = readerState.maxPages,
        pageUrls = readerState.pageUrls,
        nextButtonClicked = nextButtonClicked,
        prevButtonClicked = prevButtonClicked,
        middleButtonClicked = toggleShowDetail,
    )

    ReaderUI(
        modifier = modifier,
        showDetail = showDetail,
        manga = readerState.manga ?: return,
        currentChapter = readerState.currentChapter ?: return,
        onInfoButtonClicked = { },
        closeReader = { },
        goToNextChapter = goToNextChapter,
        goToPrevChapter = goToPrevChapter,
    )
}

@Preview()
@Composable()
private fun PreviewDefaultReader() {

    val context = LocalContext.current
    ReaderTheme {
        startKoin {
            androidContext(context)

            modules(
                appModule,
                dataStoresModule,
            )
        }

        Box(Modifier.fillMaxSize()) {
            ReaderScreenLayout(
                modifier = Modifier.fillMaxSize(),
                readerState = ReaderState(
                    readerType = ReaderType.PAGE,
                    currentPage = 0,
                    maxPages = 3,
                    pageUrls = listOf("", ""),
                    pageRequests = listOf(),
                    loading = false,
                    manga = PreviewDataFactory.MANGA,
                    currentChapter = PreviewDataFactory.CHAPTER,
                ),
                nextButtonClicked = { },
                prevButtonClicked = { },
                goToNextChapter = { },
                goToPrevChapter = { },
                toggleShowDetail = { },
                showDetail = true,
            )
        }
    }
}