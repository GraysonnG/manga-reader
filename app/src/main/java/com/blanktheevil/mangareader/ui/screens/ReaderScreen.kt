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
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.LocalNavController
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.data.dto.getScanlationGroupRelationship
import com.blanktheevil.mangareader.helpers.shortTitle
import com.blanktheevil.mangareader.helpers.title
import com.blanktheevil.mangareader.navigation.navigateToMangaDetailScreen
import com.blanktheevil.mangareader.navigation.popBackStackOrGoHome
import com.blanktheevil.mangareader.ui.components.GroupButton
import com.blanktheevil.mangareader.ui.components.MangaReaderTopAppBarState
import com.blanktheevil.mangareader.ui.components.ModalSideSheet
import com.blanktheevil.mangareader.ui.components.SegmentedButton
import com.blanktheevil.mangareader.ui.components.groupButtonColors
import com.blanktheevil.mangareader.ui.reader.PageReader
import com.blanktheevil.mangareader.ui.reader.StripReader
import com.blanktheevil.mangareader.viewmodels.ReaderType
import com.blanktheevil.mangareader.viewmodels.ReaderViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.util.Locale

@Composable
fun ReaderScreen(
    chapterId: String?,
    readerViewModel: ReaderViewModel = koinViewModel(),
    setTopAppBarState: (MangaReaderTopAppBarState) -> Unit,
) {
    val uiState by readerViewModel.uiState.collectAsState()
    val systemUIController = rememberSystemUiController()
    val primaryColor = MaterialTheme.colorScheme.secondaryContainer
    val navController = LocalNavController.current

    OnMount {
        chapterId?.let { cId ->
            readerViewModel.initReader(
                chapterId =
                if (uiState.currentChapter == null)
                    cId
                else
                    uiState.currentChapter!!.id,
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
                navController.navigateToMangaDetailScreen(uiState.manga!!.id, true)
            }
        }
    }

    setTopAppBarState(
        MangaReaderTopAppBarState(
            show = false
        )
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ReaderLayout(
            readerType = uiState.readerType,
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
            onLastPageViewed = readerViewModel::onLastPageViewed,
            selectReaderType = readerViewModel::selectReaderType,
        )
    }
}

@Composable
private fun ReaderLayout(
    readerType: ReaderType,
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
    onLastPageViewed: () -> Unit = {},
    selectReaderType: (Int) -> Unit,
) {
    var showDetail by remember { mutableStateOf(showDetailDefault) }
    var showInfoPanel by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()
    ) {
        if (!loading) {
            when (readerType) {
                ReaderType.PAGE -> {
                    PageReader(
                        currentPage = currentPage,
                        maxPages = maxPages,
                        pageUrls = pageUrls,
                        nextButtonClicked = nextButtonClicked,
                        prevButtonClicked = prevPage,
                        middleButtonClicked = {
                            showDetail = !showDetail
                        },
                    )
                }

                ReaderType.VERTICAL -> {
                    StripReader(
                        pageUrls = pageUrls,
                        onScreenClick = {
                            showDetail = !showDetail
                        },
                        nextButtonClicked = goToNextChapter,
                        onLastPageViewed = onLastPageViewed,
                    )
                }

                ReaderType.HORIZONTAL -> {
                    StripReader(
                        pageUrls = pageUrls,
                        onScreenClick = {
                            showDetail = !showDetail
                        },
                        isVertical = false,
                        nextButtonClicked = goToNextChapter,
                        onLastPageViewed = onLastPageViewed,
                    )
                }
            }

            ReaderHeader(
                showDetail = showDetail,
                manga = manga,
                onInfoButtonClicked = {
                    showInfoPanel = true
                }
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
            visible = showInfoPanel,
            readerType = readerType,
            selectReaderType = selectReaderType,
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
    val scanlationGroup = currentChapter.getScanlationGroupRelationship(
        moshi = koinInject(),
    )
    val leftChevron = painterResource(id = R.drawable.round_chevron_left_24)
    val rightChevron = painterResource(id = R.drawable.round_chevron_right_24)

    Row(
        modifier = Modifier
            .background(color = Color.Black.copy(0.8f))
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
) {
    val navController = LocalNavController.current

    AnimatedVisibility(
        modifier = Modifier.align(Alignment.TopCenter),
        visible = showDetail,
        enter = slideInVertically { -it },
        exit = slideOutVertically { -it }
    ) {
        Row(
            Modifier
                .background(color = Color.Black.copy(0.8f))
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = navController::popBackStackOrGoHome,
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
                        navController.navigateToMangaDetailScreen(manga.id, true)
                    },
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
private fun InfoPanel(
    manga: MangaDto,
    chapter: ChapterDto,
    visible: Boolean,
    readerType: ReaderType,
    selectReaderType: (Int) -> Unit,
    onDismissRequest: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(10000f)
    ) {
        ModalSideSheet(
            visible = visible,
            onDismissRequest = onDismissRequest,
            title = {
                Text(
                    text = stringResource(id = R.string.reader_info_panel_title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        ) {
            InfoPanelContent(manga, chapter, selectReaderType, readerType)
        }
    }
}

@Suppress("deprecated")
@Composable
private fun InfoPanelContent(
    manga: MangaDto,
    chapter: ChapterDto,
    selectReaderType: (Int) -> Unit,
    readerType: ReaderType
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = mangaIcon, contentDescription = null)
                TextButton(onClick = { /*TODO*/ }) {
                    Text(text = manga.title, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    Icon(
                        painter = rightChevron,
                        contentDescription = null,
                        modifier = Modifier.rotate(90f)
                    )
                }

                IconButton(onClick = { /*TODO*/ }) {
                    Icon(painter = rightChevron, contentDescription = null)
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = stringResource(id = R.string.reader_info_panel_reader_mode))
                Spacer(modifier = Modifier.height(8.dp))
                SegmentedButton(
                    modifier = Modifier.fillMaxWidth(),
                    options = ReaderType.values().map {
                        it.toString().lowercase().capitalize(Locale.ROOT)
                    },
                    initialSelectedIndex = readerType.ordinal,
                    onSelected = selectReaderType,
                )
            }
        }
    }
}

@Preview
@Composable
private fun ReaderLayoutPreview() {
    DefaultPreview {
        ReaderLayout(
            readerType = ReaderType.PAGE,
            loading = false,
            currentPage = 1,
            maxPages = 4,
            currentChapter = StubData.CHAPTER,
            manga = StubData.MANGA,
            nextButtonClicked = {},
            goToNextChapter = {},
            goToPrevChapter = {},
            prevPage = {},
            selectReaderType = {},
        )
    }
}

@Preview
@Composable
private fun ReaderLayoutDetailPreview() {
    DefaultPreview {
        ReaderLayout(
            readerType = ReaderType.PAGE,
            showDetailDefault = true,
            loading = false,
            currentPage = 1,
            maxPages = 4,
            currentChapter = StubData.CHAPTER,
            manga = StubData.MANGA,
            nextButtonClicked = {},
            goToNextChapter = {},
            goToPrevChapter = {},
            prevPage = {},
            selectReaderType = {},
        )
    }
}

@Preview
@Composable
private fun ReaderLayoutLoadingPreview() {
    DefaultPreview {
        ReaderLayout(
            readerType = ReaderType.PAGE,
            loading = true,
            currentPage = 1,
            maxPages = 4,
            currentChapter = StubData.CHAPTER,
            manga = StubData.MANGA,
            nextButtonClicked = {},
            goToNextChapter = {},
            goToPrevChapter = {},
            prevPage = {},
            selectReaderType = {},
        )
    }
}

@Preview
@Composable
private fun ReaderInfoPanelPreview() {
    DefaultPreview {
        InfoPanel(
            manga = StubData.MANGA,
            chapter = StubData.CHAPTER,
            visible = true,
            selectReaderType = {},
            onDismissRequest = {},
            readerType = ReaderType.PAGE,
        )
    }
}
