package com.blanktheevil.mangareader.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.PreviewDataFactory
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.helpers.getCoverImageUrl
import com.blanktheevil.mangareader.helpers.title
import com.blanktheevil.mangareader.ui.components.ChapterButton
import com.blanktheevil.mangareader.ui.components.ImageFromUrl
import com.blanktheevil.mangareader.ui.components.ImageLargeTopAppBar
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.viewmodels.MangaDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaDetailScreen(
    mangaDetailViewModel: MangaDetailViewModel = viewModel(),
    id: String?,
    setTopAppBar: (@Composable () -> Unit) -> Unit,
    popBackStack: () -> Unit,
    navigateToReader: (String, String) -> Unit,
) {
    val uiState by mangaDetailViewModel.uiState.collectAsState()
    val context = LocalContext.current

    OnMount {
        id?.let {
            mangaDetailViewModel.getMangaDetails(id, context)
        }
    }

    setTopAppBar @Composable {
        TopAppBar(
            title = { },
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

    if (!uiState.loading) {
        uiState.data?.let {
            MangaDetailLayout(
                manga = it,
                chapters = uiState.chapters,
                chapterReadIds = uiState.chapterReadIds,
                setTopAppBar = setTopAppBar,
                popBackStack = popBackStack,
                navigateToReader = navigateToReader,
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MangaDetailLayout(
    manga: MangaDto,
    chapters: List<ChapterDto>,
    chapterReadIds: List<String>,
    setTopAppBar: (@Composable () -> Unit) -> Unit,
    popBackStack: () -> Unit,
    navigateToReader: (String, String) -> Unit,
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)
    var selectedTabIndex by rememberSaveable {
        mutableStateOf(1)
    }
    val tabs = listOf("Description", "Chapters", "More...")

    setTopAppBar {
        ImageLargeTopAppBar(
            src = manga.getCoverImageUrl(),
            title = { Text(
                text = manga.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            ) },
            navigationIcon = {
                IconButton(onClick = popBackStack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            },
            scrollBehavior = scrollBehavior,
        )
    }

    Column(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            indicator = {
                TabIndicator(
                    selectedTabIndex = selectedTabIndex,
                    tabPositions = it
                )
            },
            divider = {
                Divider(
                    color = MaterialTheme.colorScheme.primary
                )
            },
        ) {
            tabs.forEachIndexed { index, s ->
                Tab(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                ) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text=s
                    )
                }
            }
        }

        Box(
            modifier = Modifier.padding(8.dp)
        ) {
            when (selectedTabIndex) {
                0 -> {
                    DescriptionTab(manga = manga)
                }
                1 -> {
                    ChaptersTab(
                        mangaId = manga.id,
                        list = chapters,
                        chapterReadIds = chapterReadIds,
                        navigateToReader = navigateToReader
                    )
                }
            }
        }
    }
}

@Composable
private fun DescriptionTab(
    manga: MangaDto,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        manga.getCoverImageUrl()?.let {
            Box(modifier = Modifier.fillMaxWidth(0.6f)) {
                ImageFromUrl(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(11f / 16f)
                        .clip(RoundedCornerShape(8.dp)),
                    url = it
                )
            }
        }
        manga.attributes.description["en"]?.let {
            Text(text = it)
        }
    }
}

@Composable
private fun ChaptersTab(
    mangaId: String,
    list: List<ChapterDto>,
    chapterReadIds: List<String>,
    navigateToReader: (String, String) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(list) {
            ChapterButton(
                mangaId = mangaId,
                chapter = it,
                navigateToReader = navigateToReader,
                isRead = chapterReadIds.contains(it.id),
            )
        }
    }
}

@Composable
private fun TabIndicator(
    selectedTabIndex: Int,
    tabPositions: List<TabPosition>
) {
    if (selectedTabIndex < tabPositions.size) {
        Box(
            modifier = Modifier
                .tabIndicatorOffset(tabPositions[selectedTabIndex])
                .background(Color.Transparent)
                .fillMaxWidth()
                .height(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 4.dp,
                        )
                    )
                    .background(MaterialTheme.colorScheme.primary)
                    .fillMaxWidth(0.33f)
                    .height(4.dp)
                    .align(Alignment.BottomCenter)

            ) {}
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    MangaReaderTheme {
        MangaDetailLayout(
            manga = PreviewDataFactory.MANGA,
            chapters = PreviewDataFactory.CHAPTER_LIST,
            chapterReadIds = emptyList(),
            setTopAppBar = {},
            popBackStack = {},
            navigateToReader = {_,_->},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDescriptionTab() {
    MangaReaderTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            DescriptionTab(manga = PreviewDataFactory.MANGA)
        }
    }
}