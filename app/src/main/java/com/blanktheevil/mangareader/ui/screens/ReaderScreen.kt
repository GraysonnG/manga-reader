package com.blanktheevil.mangareader.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.blanktheevil.mangareader.ui.theme.Purple40
import com.blanktheevil.mangareader.viewmodels.ReaderViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlin.math.max

@Composable
fun ReaderScreen(
    chapterId: String?,
    mangaId: String?,
    readerViewModel: ReaderViewModel = viewModel(),
    setTopAppBar: (@Composable () -> Unit) -> Unit,
    navigateToMangaDetailScreen: (String, Boolean) -> Unit,
    navigateBack: () -> Unit,
) {
    val uiState by readerViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val systemUIController = rememberSystemUiController()

    OnMount {
        letIfNotNull(chapterId, mangaId) { cId, mId ->
            readerViewModel.setOnEndOfFeedListener {
                navigateToMangaDetailScreen(mId, true)
            }

            readerViewModel.initReader(
                chapterId =
                    if (uiState.currentChapter == null)
                        cId
                    else
                        uiState.currentChapter!!.id,
                mangaId = mId,
                context = context
            )
        }
    }

    setTopAppBar {}

    DisposableEffect(Unit) {
        systemUIController.setStatusBarColor(
            color = Color.Black,
        )

        onDispose {
            systemUIController.setStatusBarColor(
                color = Purple40,
                darkIcons = true
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ReaderLayout(
            loading = uiState.loading,
            currentChapter = uiState.currentChapter ?: return,
            manga = uiState.manga ?: return,
            currentPage = uiState.currentPage,
            maxPages = uiState.maxPages,
            pageRequests = uiState.pageRequests,
            setTopAppBar = setTopAppBar,
            nextButtonClicked = readerViewModel::nextButtonClicked,
            goToNextChapter = readerViewModel::nextChapter,
            goToPrevChapter = readerViewModel::prevChapter,
            prevPage = readerViewModel::prevPage,
            navigateToMangaDetailScreen = navigateToMangaDetailScreen,
            navigateBack = navigateBack,
        )
    }
}

@Composable
private fun ReaderLayout(
    showDetailDefault: Boolean = false,
    loading: Boolean,
    currentPage: Int,
    maxPages: Int,
    currentChapter: ChapterDto,
    manga: MangaDto,
    pageRequests: List<ImageRequest>,
    setTopAppBar: (@Composable () -> Unit) -> Unit,
    nextButtonClicked: (Context) -> Unit,
    goToNextChapter: (Context) -> Unit,
    goToPrevChapter: (Context) -> Unit,
    prevPage: () -> Unit,
    navigateToMangaDetailScreen: (String, Boolean) -> Unit,
    navigateBack: () -> Unit,
) {
    var showDetail by remember { mutableStateOf(showDetailDefault) }

    setTopAppBar {}

    Box(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        if (!loading) {
            ReaderPages(currentPage = currentPage, pageRequests = pageRequests)

            ReaderUI(
                currentPage = currentPage,
                maxPages = maxPages,
                nextButtonClicked = nextButtonClicked,
                prevPage = prevPage,
                middleButtonClicked = { showDetail = !showDetail },
            )

            ReaderHeader(
                showDetail = showDetail,
                manga = manga,
                navigateToMangaDetailScreen = navigateToMangaDetailScreen,
                navigateBack = navigateBack,
            )

            AnimatedVisibility(
                modifier = Modifier.align(Alignment.BottomCenter),
                visible = showDetail,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
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
private fun BoxScope.ReaderHeader(
    showDetail: Boolean,
    manga: MangaDto,
    navigateToMangaDetailScreen: (String, Boolean) -> Unit,
    navigateBack: () -> Unit,
) {
    AnimatedVisibility(
        modifier = Modifier.align(Alignment.TopCenter),
        visible = showDetail,
        enter = slideInVertically { -it },
        exit = slideOutVertically { -it }
    ) {
        Row(
            Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = navigateBack,
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = Color.White,
                )
            }
            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f, fill = true)
                    .clickable {
                        navigateToMangaDetailScreen(manga.id, true)
                    }
                ,
                text = manga.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
            IconButton(onClick = { /*TODO*/ }, enabled = false,) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                )
            }
        }
    }
}

@Composable
private fun ReaderPages(
    currentPage: Int,
    pageRequests: List<ImageRequest>,
) {
    if (pageRequests.isNotEmpty()) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = pageRequests[currentPage],
            contentDescription = null,
            contentScale = ContentScale.Fit
        )
    }
}


@Composable
private fun ReaderUI(
    currentPage: Int,
    maxPages: Int,
    nextButtonClicked: (context: Context) -> Unit,
    prevPage: () -> Unit,
    middleButtonClicked: () -> Unit,
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
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.Transparent,
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
            pageRequests = emptyList(),
            currentChapter = PreviewDataFactory.CHAPTER,
            manga = PreviewDataFactory.MANGA,
            nextButtonClicked = {},
            goToNextChapter = {},
            goToPrevChapter = {},
            prevPage = {},
            navigateToMangaDetailScreen = { _, _ -> },
            navigateBack = {},
            setTopAppBar = {}
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
            pageRequests = emptyList(),
            currentChapter = PreviewDataFactory.CHAPTER,
            manga = PreviewDataFactory.MANGA,
            nextButtonClicked = {},
            goToNextChapter = {},
            goToPrevChapter = {},
            prevPage = {},
            navigateToMangaDetailScreen = { _, _ -> },
            navigateBack = {},
            setTopAppBar = {}
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
            pageRequests = emptyList(),
            currentChapter = PreviewDataFactory.CHAPTER,
            manga = PreviewDataFactory.MANGA,
            nextButtonClicked = {},
            goToNextChapter = {},
            goToPrevChapter = {},
            prevPage = {},
            navigateToMangaDetailScreen = { _, _ -> },
            navigateBack = {},
            setTopAppBar = {}
        )
    }
}
