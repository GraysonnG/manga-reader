package com.blanktheevil.mangareader.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.blanktheevil.mangareader.ChapterMap
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.LocalNavController
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.ChapterList
import com.blanktheevil.mangareader.data.Manga
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.toChapterList
import com.blanktheevil.mangareader.data.toManga
import com.blanktheevil.mangareader.domain.UserListsState
import com.blanktheevil.mangareader.helpers.toAsyncPainterImage
import com.blanktheevil.mangareader.toVolumeMap
import com.blanktheevil.mangareader.ui.RoundedCornerSmall
import com.blanktheevil.mangareader.ui.components.ChapterButton2
import com.blanktheevil.mangareader.ui.components.ExpandableContainer
import com.blanktheevil.mangareader.ui.components.ExpandableContentFab
import com.blanktheevil.mangareader.ui.components.ImageFromUrl
import com.blanktheevil.mangareader.ui.components.LabeledCheckbox
import com.blanktheevil.mangareader.ui.components.MangaReaderTopAppBarState
import com.blanktheevil.mangareader.ui.smallPadding
import com.blanktheevil.mangareader.ui.smallPaddingVertical
import com.blanktheevil.mangareader.ui.theme.GREEN_50
import com.blanktheevil.mangareader.ui.xLargeDp
import com.blanktheevil.mangareader.viewmodels.MangaDetailViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun MangaDetailScreen(
    mangaId: String,
    detailViewModel: MangaDetailViewModel = koinViewModel(),
    setTopAppBarState: (MangaReaderTopAppBarState) -> Unit,
) {
    val state by detailViewModel.mangaDetail()
    val userListsState by detailViewModel.userLists()
    val uiState by detailViewModel.uiState.collectAsState()
    val manga = uiState.manga

    OnMount {
        detailViewModel.getMangaDetails(mangaId)
        setTopAppBarState(
            MangaReaderTopAppBarState(
                show = false
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.loadingManga -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            manga != null -> {
                MangaDetailLayout(
                    manga = manga,
                    mangaIsFollowed = state.mangaIsFollowed,
                    loadMore = detailViewModel::loadMore,
                    followManga = detailViewModel.mangaDetail::followManga,
                    unfollowManga = detailViewModel.mangaDetail::unfollowManga,
                    addMangaToList = detailViewModel.userLists::addMangaToList,
                    removeMangaFromList = detailViewModel.userLists::removeMangaFromList,
                    userListsState = userListsState,
                    uiState = uiState,
                )
            }

            else -> {
                Text("There was an Error...")
            }
        }
    }
}

@Composable
private fun MangaDetailLayout(
    manga: Manga,
    mangaIsFollowed: Boolean,
    userListsState: UserListsState,
    uiState: MangaDetailViewModel.State,
    loadMore: () -> Unit,
    followManga: () -> Unit,
    unfollowManga: () -> Unit,
    addMangaToList: (String, String, () -> Unit) -> Unit,
    removeMangaFromList: (String, String, () -> Unit) -> Unit,
) {
    val snackbarHost = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val followFabIcon = if (mangaIsFollowed) {
        painterResource(id = R.drawable.round_bookmark_24)
    } else {
        painterResource(id = R.drawable.round_bookmark_border_24)
    }
    var openDialog by remember { mutableStateOf(false) }

    val followFabContainerColor = if (mangaIsFollowed) {
        GREEN_50
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }

    val followFabContentColor = if (mangaIsFollowed) {
        Color.Black
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }

    val onFollowButtonClicked by rememberUpdatedState(newValue = {
        if (mangaIsFollowed) unfollowManga()
        else {
            followManga()
            coroutineScope.launch {
                snackbarHost.showSnackbar(
                    message = "Followed: ${manga.title}"
                )
            }
            Unit
        }
    })

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHost) {
                Snackbar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ) {
                    Text(it.visuals.message)
                }
            }
        },
        floatingActionButton = {
            Column(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .height(IntrinsicSize.Min)
                    .width(IntrinsicSize.Min),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End,
            ) {
                AddToListFab(
                    manga = manga,
                    userListsState = userListsState,
                    shouldExpand = openDialog,
                    addMangaToList = addMangaToList,
                    removeMangaFromList = removeMangaFromList,
                ) {
                    openDialog = !openDialog
                }

                ExtendedFloatingActionButton(
                    expanded = false,
                    text = { Text("Follow") },
                    icon = { Icon(followFabIcon, contentDescription = null) },
                    onClick = onFollowButtonClicked,
                    containerColor = followFabContainerColor,
                    contentColor = followFabContentColor,
                )
            }
        }
    ) {
        val navController = LocalNavController.current

        Box {
            Surface(
                modifier = Modifier
                    .zIndex(100f)
                    .align(Alignment.TopStart)
                    .padding(8.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color.Black.copy(0.6f),
                contentColor = Color.White,
            ) {
                IconButton(
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                    ),
                    onClick = navController::popBackStack
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                CoverArtDisplay(manga.coverArt)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .padding(bottom = 16.dp)
                        .animateContentSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    MangaTitle(manga.title)
                    MangaDescription(description = manga.description)
                    Spacer(Modifier.height(32.dp))

                    ListVolumes2(
                        volumeMap = uiState.volumes,
                        coverMap = uiState.covers,
                    )

                    if (
                        !uiState.loadingVolumes &&
                        !uiState.loadedAllVolumes &&
                        !uiState.loadingMore
                    ) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(),
                            onClick = loadMore,
                            contentPadding = PaddingValues(
                                start = 24.dp,
                                end = 6.dp,
                                top = 8.dp,
                                bottom = 8.dp
                            ),
                        ) {
                            Text("Show More...")
                        }
                    }

                    if (uiState.loadingVolumes || uiState.loadingMore) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(
                                Alignment.CenterHorizontally
                            )
                        )
                    }
                }
            }
        }

        AnimatedVisibility(visible = openDialog, enter = fadeIn(), exit = fadeOut()) {
            Box(
                Modifier
                    .background(Color.Black.copy(0.3f))
                    .fillMaxSize()
                    .clickable {
                        openDialog = false
                    }
            )
        }
    }
}

@Composable
private fun CoverArtDisplay(coverArtUrl: String?) {
    coverArtUrl?.let {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            contentAlignment = Alignment.Center
        ) {
            ImageFromUrl(
                url = coverArtUrl,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(10.dp, BlurredEdgeTreatment.Rectangle)
            )
            Box(
                modifier = Modifier
                    .background(Color.Black.copy(0.75f))
                    .fillMaxSize()
            )

            Box(modifier = Modifier.fillMaxWidth(0.8f)) {
                ImageFromUrl(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(11f / 16f)
                        .clip(RoundedCornerShape(8.dp)),
                    url = coverArtUrl
                )
            }
        }
    }
}

@Composable
private fun MangaTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = (32 * 1.2f).sp,
            letterSpacing = (-0.3).sp
        ),
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun MangaDescription(description: String) {
    MarkdownText(
        modifier = Modifier.fillMaxWidth(),
        markdown = description,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.bodyLarge,
    )
}

@Composable
private fun ListVolumes2(
    volumeMap: Map<String, MutableMap<String, ChapterMap>>,
    coverMap: Map<String, String>,
) {
    volumeMap.forEach { (volumeNumber, value) ->
        key(volumeNumber) {
            ExpandableContainer(
                shape = RoundedCornerSmall,
                startExpanded = true,
                background = {
                    val mangaImage = coverMap[volumeNumber].toAsyncPainterImage(
                        crossfade = true
                    )

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
                            .background(halfBlackToBlack)
                            .fillMaxWidth()
                            .height(xLargeDp)
                    )
                },
                title = {
                    Text(
                        text = "Volume $volumeNumber",
                    )
                },
                titleContentColor = if (coverMap[volumeNumber] != null) {
                    Color.White
                } else {
                    MaterialTheme.colorScheme.onPrimaryContainer
                },
            ) {
                ListChapters2(value)
            }
        }
    }
}

@Composable
private fun ListChapters2(
    chapters: Map<String, ChapterMap>
) {
    chapters.forEach { (chapterNumber, value) ->
        key(chapterNumber) {
            if (value.size > 1) {
                GroupedChapter(
                    title = "Chapter $chapterNumber",
                    list = value.values.toList(),
                )
            } else {
                ChapterButton2(
                    chapter = value.values.first(),
                    useMediumTitle = true,
                )
            }
        }
    }
}

@Composable
private fun GroupedChapter(
    title: String,
    list: ChapterList,
) {
    Surface(
        modifier = Modifier
            .smallPaddingVertical(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerSmall,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .smallPadding(),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            HorizontalDivider(
                modifier = Modifier.smallPaddingVertical()
            )
            list.forEach {
                ChapterButton2(chapter = it, useShortTitle = true)
            }
        }
    }
}

private val halfBlackToBlack = Brush.linearGradient(
    colors = listOf(
        Color.Black.copy(alpha = 0.0f),
        Color.Black.copy(alpha = 0.0f),
        Color.Black.copy(alpha = 0.6f),
    ),
    start = Offset.Infinite.copy(y = 0f),
    end = Offset.Zero,
)


@Composable
private fun AddToListFab(
    manga: Manga,
    userListsState: UserListsState,
    shouldExpand: Boolean,
    addMangaToList: (String, String, () -> Unit) -> Unit,
    removeMangaFromList: (String, String, () -> Unit) -> Unit,
    onClick: () -> Unit,
) {
    val addToListFabIcon = painterResource(id = R.drawable.round_list_add_24)

    ExpandableContentFab(
        shouldExpand = shouldExpand,
        onClick = onClick,
        icon = { Icon(addToListFabIcon, null) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Add to a list")
            if (!userListsState.loading) {
                userListsState.data.forEach { (userList, mangaIds) ->

                    var checked by remember { mutableStateOf(manga.id in mangaIds) }

                    LabeledCheckbox(
                        text = userList.attributes.name,
                        checked = checked,
                        onCheckedChange = {
                            if (it) {
                                addMangaToList(manga.id, userList.id) {
                                    checked = true
                                }
                            } else {
                                removeMangaFromList(manga.id, userList.id) {
                                    checked = false
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewLayout() {
    DefaultPreview {
        Surface {
            MangaDetailLayout(
                manga = StubData.MANGA.toManga(),
                mangaIsFollowed = false,
                loadMore = {},
                followManga = {},
                unfollowManga = {},
                userListsState = UserListsState(loading = false),
                addMangaToList = { _, _, _ -> },
                removeMangaFromList = { _, _, _ -> },
                uiState = MangaDetailViewModel.State(
                    volumes = StubData.CHAPTER_LIST.toChapterList(
                        moshi = koinInject(),
                    ).toVolumeMap()
                )
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, heightDp = 2000)
@Composable
private fun PreviewLayoutDark() {
    DefaultPreview {
        Surface {
            MangaDetailLayout(
                manga = StubData.MANGA.toManga(),
                mangaIsFollowed = false,
                loadMore = {},
                followManga = {},
                unfollowManga = {},
                userListsState = UserListsState(loading = false),
                addMangaToList = { _, _, _ -> },
                removeMangaFromList = { _, _, _ -> },
                uiState = MangaDetailViewModel.State(
                    volumes = StubData.CHAPTER_LIST.toChapterList(
                        moshi = koinInject(),
                    ).toVolumeMap(),
                    loadingMore = false,
                    loadingVolumes = false,
                )
            )
        }
    }
}