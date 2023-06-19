package com.blanktheevil.mangareader.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.PreviewDataFactory
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.helpers.title
import com.blanktheevil.mangareader.letIfNotNull
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.viewmodels.ReaderViewModel
import kotlinx.coroutines.delay
import kotlin.math.max

@Composable
fun ReaderScreen(
    chapterId: String?,
    mangaId: String?,
    readerViewModel: ReaderViewModel = viewModel(),
    navigateToMangaDetailScreen: (String, Boolean) -> Unit,
    navigateBack: () -> Unit,
) {
    val uiState by readerViewModel.uiState.collectAsState()
    val context = LocalContext.current

    OnMount {
        letIfNotNull(chapterId, mangaId) { cId, mId ->
            readerViewModel.setOnEndOfFeedListener {
                navigateToMangaDetailScreen(mId, true)
            }

            readerViewModel.initReader(
                chapterId = cId,
                mangaId = mId,
                context = context
            )
        }
    }

    ReaderLayout(
        loading = uiState.loading,
        currentChapter = uiState.currentChapter ?: return,
        manga = uiState.manga ?: return,
        currentPage = uiState.currentPage,
        maxPages = uiState.maxPages,
        pageUrls = uiState.pageUrls,
        nextButtonClicked = readerViewModel::nextButtonClicked,
        goToNextChapter = readerViewModel::nextChapter,
        goToPrevChapter = readerViewModel::prevChapter,
        prevPage = readerViewModel::prevPage,
        navigateToMangaDetailScreen = navigateToMangaDetailScreen,
        navigateBack = navigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReaderLayout(
    showDetailDefault: Boolean = false,
    loading: Boolean,
    currentPage: Int,
    maxPages: Int,
    currentChapter: ChapterDto,
    manga: MangaDto,
    pageUrls: List<String>,
    nextButtonClicked: (Context) -> Unit,
    goToNextChapter: (Context) -> Unit,
    goToPrevChapter: (Context) -> Unit,
    prevPage: () -> Unit,
    navigateToMangaDetailScreen: (String, Boolean) -> Unit,
    navigateBack: () -> Unit,
) {
    var showDetail by remember { mutableStateOf(showDetailDefault) }

    Scaffold(
        topBar = {
            if (showDetail) {
                TopAppBar(
                    title = {
                        Text(
                            text = manga.title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = navigateBack) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        scrolledContainerColor = MaterialTheme.colorScheme.primary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = Color.Unspecified,
                    )
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .background(Color.Black)
                .fillMaxSize()
        ) {
            if (!loading) {
                ReaderPages(currentPage = currentPage, pageUrls = pageUrls)

                ReaderUI(
                    mangaId = manga.id,
                    currentPage = currentPage,
                    maxPages = maxPages,
                    nextButtonClicked = nextButtonClicked,
                    prevPage = prevPage,
                    middleButtonClicked = { showDetail = !showDetail },
                    navigateToMangaDetailScreen = navigateToMangaDetailScreen
                )

                if (showDetail) {
                    ReaderNavigator(
                        currentChapter = currentChapter,
                        goToNextChapter = goToNextChapter,
                        goToPreviousChapter = goToPrevChapter,
                    )
                }
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}

@Composable
private fun BoxScope.ReaderNavigator(
    currentChapter: ChapterDto,
    goToNextChapter: (Context) -> Unit,
    goToPreviousChapter: (Context) -> Unit,
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = { goToPreviousChapter(context) },
        ) {
            Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null, tint = Color.White)
        }

        Text(
            text = currentChapter.title,
            modifier = Modifier.weight(1f, fill = true),
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )

        IconButton(
            onClick = { goToNextChapter(context) },
        ) {
            Icon(imageVector = Icons.Rounded.ArrowForward, contentDescription = null, tint = Color.White)
        }
    }
}

@Composable
private fun ReaderPages(
    currentPage: Int,
    pageUrls: List<String>,
) {
    val context = LocalContext.current

    if (pageUrls.isNotEmpty()) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = ImageRequest.Builder(context)
                .data(pageUrls[currentPage])
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Fit
        )
    }
}


@Composable
private fun ReaderUI(
    mangaId: String?,
    currentPage: Int,
    maxPages: Int,
    nextButtonClicked: (context: Context) -> Unit,
    prevPage: () -> Unit,
    middleButtonClicked: () -> Unit,
    navigateToMangaDetailScreen: (String, Boolean) -> Unit
) {
    val progress = currentPage.toFloat().plus(1f) / max(1f, maxPages.toFloat())
    val context = LocalContext.current

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.weight(1f, fill = true)
            ) {
                Box(modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable(role = Role.Button) {
                        prevPage()
                    }
                ) {}

                Box(modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable(role = Role.Button) {
                        middleButtonClicked()
                    }
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable(role = Role.Button) {
                            nextButtonClicked(context)
                        }
                ) {}
            }

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
                color = Color.White
            )
        }
    }
}

@Preview
@Composable
private fun ReaderLayoutPreview() {
    MangaReaderTheme {
        ReaderLayout(
            loading = false,
            currentPage = 1,
            maxPages = 4,
            pageUrls = emptyList(),
            currentChapter = PreviewDataFactory.CHAPTER,
            manga = PreviewDataFactory.MANGA,
            nextButtonClicked = {},
            goToNextChapter = {},
            goToPrevChapter = {},
            prevPage = {},
            navigateToMangaDetailScreen = { _, _ -> },
            navigateBack = {}
        )
    }
}

@Preview
@Composable
private fun ReaderLayoutDetailPreview() {
    MangaReaderTheme {
        ReaderLayout(
            showDetailDefault = true,
            loading = false,
            currentPage = 1,
            maxPages = 4,
            pageUrls = emptyList(),
            currentChapter = PreviewDataFactory.CHAPTER,
            manga = PreviewDataFactory.MANGA,
            nextButtonClicked = {},
            goToNextChapter = {},
            goToPrevChapter = {},
            prevPage = {},
            navigateToMangaDetailScreen = { _, _ -> },
            navigateBack = {}
        )
    }
}
@Preview
@Composable
private fun ReaderLayoutLoadingPreview() {
    MangaReaderTheme {
        ReaderLayout(
            loading = true,
            currentPage = 1,
            maxPages = 4,
            pageUrls = emptyList(),
            currentChapter = PreviewDataFactory.CHAPTER,
            manga = PreviewDataFactory.MANGA,
            nextButtonClicked = {},
            goToNextChapter = {},
            goToPrevChapter = {},
            prevPage = {},
            navigateToMangaDetailScreen = { _, _ -> },
            navigateBack = {}
        )
    }
}
