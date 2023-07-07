package com.blanktheevil.mangareader.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.PreviewDataFactory
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.data.dto.getScanlationGroupRelationship
import com.blanktheevil.mangareader.helpers.shortTitle
import com.blanktheevil.mangareader.helpers.title
import com.blanktheevil.mangareader.ui.components.GroupButton
import com.blanktheevil.mangareader.ui.components.MangaReaderTopAppBarState
import com.blanktheevil.mangareader.ui.components.ModalSideSheet
import com.blanktheevil.mangareader.ui.components.groupButtonColors
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.viewmodels.ReaderViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlin.math.max
import kotlin.math.min

@Composable
fun ReaderScreen(
    chapterId: String?,
    readerViewModel: ReaderViewModel = viewModel(),
    setTopAppBarState: (MangaReaderTopAppBarState) -> Unit,
    navigateToMangaDetailScreen: (String, Boolean) -> Unit,
    navigateBack: () -> Unit,
) {
    val uiState by readerViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val systemUIController = rememberSystemUiController()
    val primaryColor = MaterialTheme.colorScheme.primary

    OnMount {
        chapterId?.let { cId ->
            readerViewModel.initReader(
                chapterId =
                    if (uiState.currentChapter == null)
                        cId
                    else
                        uiState.currentChapter!!.id,
                context = context
            )
        }
    }

    DisposableEffect(Unit) {
        systemUIController.setStatusBarColor(
            color = Color.Black,
        )

        onDispose {
            systemUIController.setStatusBarColor(
                color = primaryColor,
                darkIcons = true
            )
        }
    }

    LaunchedEffect(uiState.manga) {
        if (uiState.manga != null) {
            readerViewModel.setOnEndOfFeedListener {
                navigateToMangaDetailScreen(uiState.manga!!.id, true)
            }
        }
    }

    setTopAppBarState(MangaReaderTopAppBarState(
        show = false
    ))

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
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
}

@Composable
private fun ReaderLayout(
    showDetailDefault: Boolean = false,
    loading: Boolean,
    currentPage: Int,
    maxPages: Int,
    currentChapter: ChapterDto,
    manga: MangaDto,
    pageUrls: List<String> = emptyList(),
    nextButtonClicked: () -> Unit,
    goToNextChapter: () -> Unit,
    goToPrevChapter: () -> Unit,
    prevPage: () -> Unit,
    navigateToMangaDetailScreen: (String, Boolean) -> Unit,
    navigateBack: () -> Unit,
) {
    var showDetail by remember { mutableStateOf(showDetailDefault) }
    var showInfoPanel by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()
    ) {
        if (!loading) {
            ReaderPages(
                currentPage = currentPage,
                pageUrls = pageUrls,
            )

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
                onInfoButtonClicked = {
                    showInfoPanel = true
                },
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

        InfoPanel(
            manga = manga,
            chapter = currentChapter,
            visible = showInfoPanel
        ) {
            showInfoPanel = false
        }
    }
}

@Composable
private fun BoxScope.ReaderNavigator(
    currentChapter: ChapterDto,
    goToNextChapter: () -> Unit,
    goToPreviousChapter: () -> Unit,
) {
    val scanlationGroup = currentChapter.getScanlationGroupRelationship()
    val leftChevron = painterResource(id = R.drawable.round_chevron_left_24)
    val rightChevron = painterResource(id = R.drawable.round_chevron_right_24)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = { goToPreviousChapter() },
        ) {
            Icon(painter = leftChevron, contentDescription = null, tint = Color.White)
        }

        Column(
            Modifier
                .height(IntrinsicSize.Min)
                .weight(1f, fill = true),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = currentChapter.title,
                modifier = Modifier,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            scanlationGroup?.let {
                GroupButton(
                    group = it,
                    colors = groupButtonColors(
                        contentColor = Color.White,
                    )
                )
            }
        }

        IconButton(
            onClick = { goToNextChapter() },
        ) {
            Icon(painter = rightChevron, contentDescription = null, tint = Color.White)
        }
    }
}

@Composable
private fun BoxScope.ReaderHeader(
    showDetail: Boolean,
    manga: MangaDto,
    onInfoButtonClicked: () -> Unit,
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
            IconButton(onClick = onInfoButtonClicked) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun ReaderPages(
    currentPage: Int,
    pageUrls: List<String>,
) {
    if (pageUrls.isNotEmpty()) {
        val nextPage = min(currentPage + 1, pageUrls.size - 1)
        if (nextPage != currentPage) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0f),
                model = pageUrls[nextPage],
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
        }
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = pageUrls[currentPage],
            contentDescription = null,
            contentScale = ContentScale.Fit
        )
    }
}


@Composable
private fun ReaderUI(
    currentPage: Int,
    maxPages: Int,
    nextButtonClicked: () -> Unit,
    prevPage: () -> Unit,
    middleButtonClicked: () -> Unit,
) {
    val progress = currentPage.toFloat().plus(1f) / max(1f, maxPages.toFloat())

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
                            nextButtonClicked()
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

@Composable
private fun InfoPanel(
    manga: MangaDto,
    chapter: ChapterDto,
    visible: Boolean,
    onDismissRequest: () -> Unit,
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .zIndex(10000f)) {
        ModalSideSheet(
            visible = visible,
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = "Info", maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        ) {
            InfoPanelContent(manga, chapter)
        }
    }
}

@Composable
private fun InfoPanelContent(
    manga: MangaDto,
    chapter: ChapterDto,
) {
    val mangaIcon = painterResource(id = R.drawable.twotone_import_contacts_24)
    val chapterIcon = painterResource(id = R.drawable.twotone_insert_drive_file_24)
    val leftChevron = painterResource(id = R.drawable.round_chevron_left_24)
    val rightChevron = painterResource(id = R.drawable.round_chevron_right_24)

    Column(
        modifier = Modifier
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,) {
                Icon(painter = mangaIcon, contentDescription = null)
                TextButton(onClick = { /*TODO*/ }) {
                    Text(text = manga.title, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically,) {
                Icon(
                    painter = chapterIcon,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = chapter.shortTitle,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Row(Modifier.fillMaxWidth()) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(painter = leftChevron, contentDescription = null)
                }

                OutlinedButton(
                    modifier = Modifier.weight(1f, fill = true),
                    shape = RoundedCornerShape(4.dp),
                    onClick = { /*TODO*/ }
                ) {
                    Text(
                        modifier = Modifier.weight(1f, fill = true),
                        text = "Chapter ${chapter.attributes.chapter ?: "null"}",
                        maxLines = 1,
                    )
                    Icon(painter = rightChevron, contentDescription = null, modifier = Modifier.rotate(90f))
                }

                IconButton(onClick = { /*TODO*/ }) {
                    Icon(painter = rightChevron, contentDescription = null)
                }
            }
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
            currentChapter = PreviewDataFactory.CHAPTER,
            manga = PreviewDataFactory.MANGA,
            nextButtonClicked = {},
            goToNextChapter = {},
            goToPrevChapter = {},
            prevPage = {},
            navigateToMangaDetailScreen = { _, _ -> },
            navigateBack = {},
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
            currentChapter = PreviewDataFactory.CHAPTER,
            manga = PreviewDataFactory.MANGA,
            nextButtonClicked = {},
            goToNextChapter = {},
            goToPrevChapter = {},
            prevPage = {},
            navigateToMangaDetailScreen = { _, _ -> },
            navigateBack = {},
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
            currentChapter = PreviewDataFactory.CHAPTER,
            manga = PreviewDataFactory.MANGA,
            nextButtonClicked = {},
            goToNextChapter = {},
            goToPrevChapter = {},
            prevPage = {},
            navigateToMangaDetailScreen = { _, _ -> },
            navigateBack = {},
        )
    }
}

@Preview
@Composable
private fun ReaderInfoPanelPreview() {
    MangaReaderTheme {
        InfoPanel(
            manga = PreviewDataFactory.MANGA,
            chapter = PreviewDataFactory.CHAPTER,
            visible = true,
            onDismissRequest = {},
        )
    }
}
