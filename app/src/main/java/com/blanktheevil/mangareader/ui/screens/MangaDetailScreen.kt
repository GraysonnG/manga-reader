package com.blanktheevil.mangareader.ui.screens

import android.content.res.Configuration
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blanktheevil.mangareader.ChapterList
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.PreviewDataFactory
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.dto.AggregateVolumeDto
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.helpers.description
import com.blanktheevil.mangareader.helpers.getCoverImageUrl
import com.blanktheevil.mangareader.helpers.title
import com.blanktheevil.mangareader.ui.components.ChapterButton2
import com.blanktheevil.mangareader.ui.components.ExpandableContainer
import com.blanktheevil.mangareader.ui.components.ImageFromUrl
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.viewmodels.MangaDetailViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaDetailScreen(
    mangaId: String,
    detailViewModel: MangaDetailViewModel = viewModel(),
    setTopAppBar: (topAppBar: @Composable () -> Unit) -> Unit,
    navigateToReader: (String, String) -> Unit,
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val state by detailViewModel.mangaDetail()
    val manga = state.manga

    OnMount {
        detailViewModel.getMangaDetails(mangaId, context)
    }

    setTopAppBar {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = navigateBack) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        )
    }

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
                    navigateToReader = navigateToReader,
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
    followManga: () -> Unit,
    unfollowManga: () -> Unit,
    getChaptersForVolume: suspend (AggregateVolumeDto) -> ChapterList,
    navigateToReader: (String, String) -> Unit,
) = Column(
    modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(16.dp),
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
        MangaCTA(
            mangaId = manga.id,
            mangaIsFollowed = mangaIsFollowed,
            volumes = volumes,
            followManga = followManga,
            unfollowManga = unfollowManga,
            navigateToReader = navigateToReader,
        )
        MangaDescription(description = manga.description)
        ListVolumes(
            mangaId = manga.id,
            readMarkers = readMarkers,
            volumes = volumes,
            getChaptersForVolume = getChaptersForVolume,
            navigateToReader = navigateToReader,
        )
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
private fun MangaCTA(
    mangaId: String,
    mangaIsFollowed: Boolean,
    volumes: Map<String, AggregateVolumeDto>,
    followManga: () -> Unit,
    unfollowManga: () -> Unit,
    navigateToReader: (String, String) -> Unit,
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

    val followButtonIcon = painterResource(id = if (mangaIsFollowed) {
        R.drawable.round_bookmark_24
    } else {
        R.drawable.round_bookmark_border_24
    })

    val firstChapterId = volumes.values.lastOrNull()
        ?.chapters?.values?.lastOrNull()?.id

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
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = followButtonContainerColor,
                contentColor = followButtonContentColor,
            ),
        ) {
            Icon(followButtonIcon, contentDescription = null)
        }

        firstChapterId?.let {
            Button(
                onClick = {
                    navigateToReader(it, mangaId)
                },
                shape = RoundedCornerShape(4.dp),
            ) {
                Text(text = "Start Reading")
            }
        }
    }
}

@Composable
private fun MangaTitle(title: String) {
    Text(text = title, style = MaterialTheme.typography.headlineLarge)
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
    mangaId: String,
    readMarkers: List<String>,
    volumes: Map<String, AggregateVolumeDto>,
    getChaptersForVolume: suspend (AggregateVolumeDto) -> ChapterList,
    navigateToReader: (String, String) -> Unit,
) {
    volumes.entries.forEachIndexed { index, (_, volumeData ) ->
        VolumeContainer(
            index = index,
            mangaId = mangaId,
            readMarkers = readMarkers,
            volume = volumeData,
            getChaptersForVolume = getChaptersForVolume,
            navigateToReader = navigateToReader,
        )
    }
}

@Composable
private fun VolumeContainer(
    index: Int,
    mangaId: String,
    readMarkers: List<String>,
    volume: AggregateVolumeDto,
    getChaptersForVolume: suspend (AggregateVolumeDto) -> ChapterList,
    navigateToReader: (String, String) -> Unit,
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
                    mangaId = mangaId,
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
                navigateToReader = { _, _ -> }
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
                navigateToReader = { _, _ -> }
            )
        }
    }
}