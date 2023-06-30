package com.blanktheevil.mangareader.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.List
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
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
        LargeTopAppBar(
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
                mangaIsFollowed = uiState.mangaIsFollowed,
                chapters = uiState.chapters,
                chapterReadIds = uiState.chapterReadIds,
                followManga = mangaDetailViewModel::followManga,
                unFollowManga = mangaDetailViewModel::unFollowManga,
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
    mangaIsFollowed: Boolean,
    chapters: List<ChapterDto>,
    chapterReadIds: List<String>,
    setTopAppBar: (@Composable () -> Unit) -> Unit,
    followManga: () -> Unit,
    unFollowManga: () -> Unit,
    popBackStack: () -> Unit,
    navigateToReader: (String, String) -> Unit,
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)
    var selectedTabIndex by rememberSaveable {
        mutableStateOf(1)
    }
    val tabs = listOf("Info", "Chapters", "Cover Art")
    val tabIcons = listOf(
        Icons.Outlined.Info,
        Icons.Rounded.List,
        Icons.Outlined.AccountBox,
    )

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
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp)),
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                ) {
                    Icon(
                        modifier = Modifier.padding(top = 16.dp),
                        imageVector = tabIcons[index],
                        contentDescription = null
                    )
                    Text(modifier = Modifier.padding(bottom = 16.dp), text=s)
                }
            }
        }

        Box {
            when (selectedTabIndex) {
                0 -> {
                    DescriptionTab(
                        manga = manga,
                        mangaIsFollowed = mangaIsFollowed,
                        followManga = followManga,
                        unfollowManga = unFollowManga,
                    )
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
    mangaIsFollowed: Boolean,
    followManga: () -> Unit,
    unfollowManga: () -> Unit,
) {
    val followButtonContainerColor by animateColorAsState(
        targetValue = if (mangaIsFollowed) {
            MaterialTheme.colorScheme.secondary
        } else {
            Color.DarkGray
        }
    )

    val followButtonContentColor by animateColorAsState(
        targetValue = if (mangaIsFollowed) {
            MaterialTheme.colorScheme.onSecondary
        } else {
            Color.White
        }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        manga.getCoverImageUrl()?.let {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min), contentAlignment = Alignment.Center) {
                ImageFromUrl(
                    url = it,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(10.dp, BlurredEdgeTreatment.Rectangle)
                )
                Box(modifier = Modifier
                    .background(Color.Black.copy(0.75f))
                    .fillMaxSize())

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

        }
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FilledIconButton(
                    onClick = {
                        if (mangaIsFollowed) {
                            unfollowManga()
                        } else {
                            followManga()
                        }
                    },
                    colors =IconButtonDefaults.filledIconButtonColors(
                        containerColor = followButtonContainerColor,
                        contentColor = followButtonContentColor,
                    ),
                ) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null)
                }

                Button(
                    onClick = { /*TODO*/ },
                    shape = RoundedCornerShape(4.dp),
                ) {
                    Text(text = "Start Reading")
                }
            }
            manga.attributes.description?.get("en")?.let {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = it
                )
            }
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
    var chapterData: Map<ChapterDto, Boolean> by remember { mutableStateOf(emptyMap()) }

    OnMount {
        chapterData = list.associateWith {
            chapterReadIds.contains(it.id)
        }
    }

    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(chapterData.entries.toList()) {
            ChapterButton(
                mangaId = mangaId,
                chapter = it.key,
                navigateToReader = navigateToReader,
                isRead = it.value,
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
            mangaIsFollowed = false,
            followManga = {},
            unFollowManga = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDescriptionTab() {
    MangaReaderTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            DescriptionTab(
                manga = PreviewDataFactory.MANGA,
                mangaIsFollowed = true,
                followManga = {},
                unfollowManga = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewChapterTab() {
    MangaReaderTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            ChaptersTab(
                mangaId = PreviewDataFactory.MANGA.id,
                list = PreviewDataFactory.CHAPTER_LIST,
                chapterReadIds = emptyList(),
                navigateToReader = {_,_->},
            )
        }
    }
}