package com.blanktheevil.mangareader.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
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
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.helpers.getCoverImageUrl
import com.blanktheevil.mangareader.helpers.title
import com.blanktheevil.mangareader.ui.components.ChapterButton2
import com.blanktheevil.mangareader.ui.components.ExpandableContainer
import com.blanktheevil.mangareader.ui.components.MangaReaderTopAppBarState
import com.blanktheevil.mangareader.ui.theme.MangaReaderDefaults
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
    manga: MangaDto,
    getChapters: suspend (mangaId: String) -> List<ChapterDto>,
    removeChapterFromHistory: (String) -> Unit,
) {
    var chapters: List<ChapterDto> by remember { mutableStateOf(emptyList()) }
    val mangaImage = rememberAsyncImagePainter(
        model = manga.getCoverImageUrl()
    )

    Card() {
        Column(
            Modifier.padding(8.dp),
        ) {
            Row(
                Modifier.padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.25f)
                        .aspectRatio(11 / 16f),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Transparent,
                    shadowElevation = 4.dp,
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = mangaImage,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }

                Text(
                    modifier = Modifier.weight(1f, fill = true),
                    text = manga.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            ExpandableContainer(
                title = { Text("Chapters") },
                onExpand = {
                    chapters = getChapters(manga.id)
                    true
                }
            ) {
                chapters.forEach {
                    key(it.id) {
                        ChapterButton2(
                            chapter = it,
                            isRead = true,
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
    }
}

@Composable
private fun HistoryScreenLayout(
    manga: List<MangaDto>?,
    removeChapterFromHistory: (String) -> Unit,
    getChapters: suspend (mangaId: String) -> List<ChapterDto>,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(Modifier)
        manga?.forEach {
            key(it.id) {
                HistoryItem(
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
                manga = StubData.MANGA_LIST,
                getChapters = { StubData.CHAPTER_LIST },
                removeChapterFromHistory = {}
            )
        }
    }
}