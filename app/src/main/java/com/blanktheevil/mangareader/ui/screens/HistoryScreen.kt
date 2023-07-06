package com.blanktheevil.mangareader.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.PreviewDataFactory
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.helpers.getCoverImageUrl
import com.blanktheevil.mangareader.helpers.title
import com.blanktheevil.mangareader.ui.components.ChapterButton2
import com.blanktheevil.mangareader.ui.components.ExpandableContainer
import com.blanktheevil.mangareader.ui.theme.MangaReaderDefaults
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.viewmodels.HistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel = viewModel(),
    setTopAppBar: (@Composable () -> Unit) -> Unit,
    navigateBack: () -> Unit,
    navigateToReader: (String) -> Unit,
) {
    val context = LocalContext.current
    val uiState by historyViewModel.uiState.collectAsState()
    val historyIcon = painterResource(id = R.drawable.baseline_history_24)

    setTopAppBar {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(painter = historyIcon, contentDescription = null)
                    Text(text = stringResource(id = R.string.history_screen_title))
                }
            },
            colors = MangaReaderDefaults.topAppBarColors(),
        )
    }

    OnMount {
        historyViewModel.initViewModel(context)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.history != null) {
            HistoryScreenLayout(
                manga = uiState.manga,
                getChapters = historyViewModel::getChapters,
                navigateToReader = navigateToReader
            )
        } else {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun HistoryItem(
    manga: MangaDto,
    getChapters: suspend (mangaId: String) -> List<ChapterDto>,
    navigateToReader: (String) -> Unit,
) {
    var chapters: List<ChapterDto> by remember { mutableStateOf(emptyList()) }
    var mangaImage = rememberAsyncImagePainter(model = manga.getCoverImageUrl())

    ExpandableContainer(
        title = {
            Text(text = manga.title)
        },
        background = {
            Box(
                modifier = Modifier.height(IntrinsicSize.Min)
            ) {
                Image(
                    modifier = Modifier
                        .height(128.dp)
                        .fillMaxWidth()
                        .blur(5.dp, BlurredEdgeTreatment.Rectangle),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.TopCenter,
                    painter = mangaImage,
                    contentDescription = null
                )
                Box(
                    Modifier
                        .background(Color.Black.copy(0.5f))
                        .fillMaxSize()
                )
            }
        },
        onExpand = {
            chapters = getChapters(manga.id)
            true
        }
    ) {
        chapters.forEach {
            ChapterButton2(
                chapter = it,
                isRead = true,
                navigateToReader = navigateToReader
            )
        }
    }
}

@Composable
private fun HistoryScreenLayout(
    manga: List<MangaDto>?,
    getChapters: suspend (mangaId: String) -> List<ChapterDto>,
    navigateToReader: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(Modifier)
        manga?.forEach {
            HistoryItem(
                manga = it,
                getChapters = getChapters,
                navigateToReader = navigateToReader,
            )
        }
        Spacer(Modifier)
    }
}

@Preview
@Composable
private fun PreviewLight() {
    MangaReaderTheme {
        Surface(Modifier.fillMaxSize()) {
            HistoryScreenLayout(
                manga = PreviewDataFactory.MANGA_LIST,
                getChapters = { PreviewDataFactory.CHAPTER_LIST },
                navigateToReader = {}
            )
        }
    }
}