package com.blanktheevil.mangareader.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blanktheevil.mangareader.ChapterList
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.PreviewDataFactory
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.dto.AggregateVolumeDto
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.domain.UserListsState
import com.blanktheevil.mangareader.helpers.description
import com.blanktheevil.mangareader.helpers.getCoverImageUrl
import com.blanktheevil.mangareader.helpers.title
import com.blanktheevil.mangareader.ui.components.ChapterButton2
import com.blanktheevil.mangareader.ui.components.ExpandableContainer
import com.blanktheevil.mangareader.ui.components.ExpandableContentFab
import com.blanktheevil.mangareader.ui.components.ImageFromUrl
import com.blanktheevil.mangareader.ui.components.LabeledCheckbox
import com.blanktheevil.mangareader.ui.components.MangaReaderTopAppBarState
import com.blanktheevil.mangareader.ui.theme.GREEN_50
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.viewmodels.MangaDetailViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch

@Composable
fun MangaDetailScreen(
    mangaId: String,
    detailViewModel: MangaDetailViewModel = viewModel(),
    setTopAppBarState: (MangaReaderTopAppBarState) -> Unit,
    navigateToReader: (String) -> Unit,
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val state by detailViewModel.mangaDetail()
    val userListsState by detailViewModel.userLists()
    val manga = state.manga

    OnMount {
        detailViewModel.getMangaDetails(mangaId, context)
    }

    setTopAppBarState(
        MangaReaderTopAppBarState(
            navigateBack = navigateBack,
            colored = false
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            manga != null -> {
                MangaDetailLayout(
                    manga = manga,
                    mangaIsFollowed = state.mangaIsFollowed,
                    volumes = state.volumes,
                    readMarkers = state.readIds,
                    getChaptersForVolume = detailViewModel::getChaptersForVolume,
                    followManga = detailViewModel.mangaDetail::followManga,
                    unfollowManga = detailViewModel.mangaDetail::unfollowManga,
                    addMangaToList = detailViewModel.userLists::addMangaToList,
                    removeMangaFromList = detailViewModel.userLists::removeMangaFromList,
                    navigateToReader = navigateToReader,
                    userListsState = userListsState,
                )
            }
            else -> {
                Text("Error")
            }
        }
    }
}

@Composable
private fun MangaDetailLayout(
    manga: MangaDto,
    mangaIsFollowed: Boolean,
    volumes: Map<String, AggregateVolumeDto> = emptyMap(),
    readMarkers: List<String>,
    userListsState: UserListsState,
    followManga: () -> Unit,
    unfollowManga: () -> Unit,
    addMangaToList: (String, String, () -> Unit) -> Unit,
    removeMangaFromList: (String, String, () -> Unit) -> Unit,
    getChaptersForVolume: suspend (AggregateVolumeDto) -> ChapterList,
    navigateToReader: (String) -> Unit,
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

        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            CoverArtDisplay(manga.getCoverImageUrl())
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                MangaTitle(manga.title)
                MangaDescription(description = manga.description)
                Spacer(Modifier.height(32.dp))
                ListVolumes(
                    readMarkers = readMarkers,
                    volumes = volumes,
                    getChaptersForVolume = getChaptersForVolume,
                    navigateToReader = navigateToReader,
                )
            }
        }

        AnimatedVisibility(visible = openDialog, enter = fadeIn(), exit = fadeOut(),) {
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
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min), contentAlignment = Alignment.Center) {
            ImageFromUrl(
                url = coverArtUrl,
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
private fun ListVolumes(
    readMarkers: List<String>,
    volumes: Map<String, AggregateVolumeDto>,
    getChaptersForVolume: suspend (AggregateVolumeDto) -> ChapterList,
    navigateToReader: (String) -> Unit,
) {
    volumes.entries.forEachIndexed { index, (_, volumeData ) ->
        VolumeContainer(
            index = index,
            readMarkers = readMarkers,
            volume = volumeData,
            getChaptersForVolume = getChaptersForVolume,
            navigateToReader = navigateToReader,
        )
    }
}

@Composable
private fun AddToListFab(
    manga: MangaDto,
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

@Composable
private fun VolumeContainer(
    index: Int,
    readMarkers: List<String>,
    volume: AggregateVolumeDto,
    getChaptersForVolume: suspend (AggregateVolumeDto) -> ChapterList,
    navigateToReader: (String) -> Unit,
) {
    var chapters: ChapterList by remember { mutableStateOf(emptyList()) }

    ExpandableContainer(
        startExpanded = index == 0,
        title = {
            Text(
                text = volume.volume?.let { "Volume $it" } ?: "None",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        },
        onExpand = {
            chapters = getChaptersForVolume(volume)
            true
        }
    ) {
        Column {
            chapters.forEach {
                ChapterButton2(
                    chapter = it,
                    isRead = it.id in readMarkers,
                    navigateToReader = navigateToReader,
                    useShortTitle = true,
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewLayout() {
    MangaReaderTheme {
        Surface {
            MangaDetailLayout(
                manga = PreviewDataFactory.MANGA,
                volumes = mapOf(
                    "1" to PreviewDataFactory.VOLUME_AGGREGATE
                ),
                readMarkers = emptyList(),
                mangaIsFollowed = false,
                followManga = { /*TODO*/ },
                unfollowManga = { /*TODO*/ },
                getChaptersForVolume = { PreviewDataFactory.CHAPTER_LIST },
                navigateToReader = {},
                userListsState = UserListsState(loading = false),
                addMangaToList = {_,_,_ -> },
                removeMangaFromList = {_,_,_ -> },
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewLayoutDark() {
    MangaReaderTheme {
        Surface {
            MangaDetailLayout(
                manga = PreviewDataFactory.MANGA,
                volumes = mapOf(
                    "1" to PreviewDataFactory.VOLUME_AGGREGATE
                ),
                readMarkers = emptyList(),
                mangaIsFollowed = false,
                followManga = { /*TODO*/ },
                unfollowManga = { /*TODO*/ },
                getChaptersForVolume = { PreviewDataFactory.CHAPTER_LIST },
                navigateToReader = {},
                userListsState = UserListsState(loading = false),
                addMangaToList = {_,_,_ -> },
                removeMangaFromList = {_,_,_ -> },
            )
        }
    }
}