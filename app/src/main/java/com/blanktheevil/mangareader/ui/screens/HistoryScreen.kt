package com.blanktheevil.mangareader.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.OnUIError
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.Manga
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.dto.utils.ChapterList
import com.blanktheevil.mangareader.data.dto.utils.MangaList
import com.blanktheevil.mangareader.data.dto.utils.chapter.toChapterList
import com.blanktheevil.mangareader.data.dto.utils.manga.toMangaList
import com.blanktheevil.mangareader.ui.RoundedCornerSmall
import com.blanktheevil.mangareader.ui.components.ChapterButton
import com.blanktheevil.mangareader.ui.components.ExpandableContainer
import com.blanktheevil.mangareader.ui.components.MangaReaderTopAppBarState
import com.blanktheevil.mangareader.ui.theme.MangaReaderDefaults
import com.blanktheevil.mangareader.ui.xLargeDp
import com.blanktheevil.mangareader.ui.xSmallPaddingHorizontal
import com.blanktheevil.mangareader.viewmodels.HistoryViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel = koinViewModel(),
    setTopAppBarState: (MangaReaderTopAppBarState) -> Unit,
) {
    val uiState by historyViewModel.uiState.collectAsState()
    val historyIcon = painterResource(id = R.drawable.baseline_history_24)
    val snackbarHostState = remember { SnackbarHostState() }

    setTopAppBarState(
        MangaReaderTopAppBarState(
            title = stringResource(id = R.string.history_screen_title),
            titleIcon = historyIcon,
        )
    )

    OnMount {
        historyViewModel.initViewModel()
    }

    OnUIError(error = uiState.error) {
        snackbarHostState.showSnackbar(
            message = it.getErrorTitle(),
            duration = SnackbarDuration.Short
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.padding(12.dp),
                hostState = snackbarHostState
            ) {
                uiState.error?.let {
                    MangaReaderDefaults.DefaultErrorSnackBar(
                        snackbarHostState = snackbarHostState,
                        error = it
                    )
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            if (uiState.history != null) {
                HistoryScreenLayout(
                    manga = uiState.manga,
                    getChapters = historyViewModel::getChapters,
                    removeChapterFromHistory = historyViewModel::removeChapterFromHistory,
                )
            } else {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun HistoryItem(
    index: Int,
    manga: Manga,
    getChapters: suspend (mangaId: String) -> ChapterList,
    removeChapterFromHistory: (String) -> Unit,
) {
    var chapters: ChapterList by remember { mutableStateOf(emptyList()) }
    val mangaImage = rememberAsyncImagePainter(
        model = manga.coverArt
    )

    ExpandableContainer(
        modifier = Modifier
            .xSmallPaddingHorizontal(),
        title = {
            Text(
                text = manga.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Black
                )
            )
        },
        background = {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(xLargeDp)
                    .blur(20.dp, BlurredEdgeTreatment.Rectangle),
                painter = mangaImage,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )

            Box(
                Modifier
                    .background(Color.Black.copy(alpha = 0.3f))
                    .fillMaxWidth()
                    .height(xLargeDp)
            )
        },
        shape = RoundedCornerSmall,
        titleContentColor = Color.White,
        onExpand = {
            chapters = getChapters(manga.id)
            true
        },
        startExpanded = index == 0
    ) {
        chapters.forEach {
            key(it.id) {
                ChapterButton(
                    chapter = it.copy(
                        isRead = true
                    ),
                    followingIcon = {
                        IconButton(onClick = {
                            chapters = chapters.filter { c -> c.id != it.id }
                            removeChapterFromHistory(it.id)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.round_close_24),
                                contentDescription = null,
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun HistoryScreenLayout(
    manga: MangaList?,
    removeChapterFromHistory: (String) -> Unit,
    getChapters: suspend (mangaId: String) -> ChapterList,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(Modifier)
        manga?.forEachIndexed { ind, it ->
            key(it.id) {
                HistoryItem(
                    index = ind,
                    manga = it,
                    getChapters = getChapters,
                    removeChapterFromHistory = removeChapterFromHistory
                )
            }
        }
        Spacer(Modifier)
    }
}

@Preview
@Composable
private fun PreviewLight() {
    DefaultPreview {
        Surface(Modifier.fillMaxSize()) {
            HistoryScreenLayout(
                manga = StubData.Data.MANGA_LIST.toMangaList(),
                getChapters = { StubData.Data.CHAPTER_LIST.toChapterList() },
                removeChapterFromHistory = {}
            )
        }
    }
}