package com.blanktheevil.mangareader.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.LocalNavController
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.domain.ChapterFeedState
import com.blanktheevil.mangareader.helpers.getCoverImageUrl
import com.blanktheevil.mangareader.helpers.title
import com.blanktheevil.mangareader.helpers.toAsyncPainterImage
import com.blanktheevil.mangareader.navigation.navigateToMangaDetailScreen
import com.valentinilk.shimmer.shimmer

typealias ChapterFeedItems = Map<MangaDto, List<Pair<ChapterDto, Boolean>>>

private val CARD_BORDER_RADIUS = 4.dp

@Composable
fun ChapterFeed(
    chapterFeedState: ChapterFeedState,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Spacer(Modifier)
        if (chapterFeedState.loading) {
            List(4) {}.forEach { _ ->
                ShimmerFeedCard()
            }
        } else {
            val items = remember { chapterFeedState.chapterFeedItems.entries.toList() }

            items.forEach { (manga, chapters) ->
                key(manga.id) {
                    ChapterFeedCard(
                        manga = manga,
                        chapters = chapters,
                    )
                }
            }
            Spacer(Modifier)
        }
    }
}

@Composable
private fun ChapterFeedCard(
    manga: MangaDto,
    chapters: List<Pair<ChapterDto, Boolean>>,
) {
    val navController = LocalNavController.current
    val thumbnail = manga.getCoverImageUrl()
        .toAsyncPainterImage(
            crossfade = true
        )

    Card(
        shape = RoundedCornerShape(CARD_BORDER_RADIUS),
    ) {
        Text(
            modifier = Modifier
                .clickable(
                    role = Role.Button,
                    onClick = { navController.navigateToMangaDetailScreen(manga.id) }
                )
                .fillMaxWidth()
                .padding(8.dp),
            text = manga.title,
            style = MaterialTheme.typography.titleMedium
        )
        Row(
            Modifier
                .padding(horizontal = 8.dp)
                .padding(bottom = 8.dp),
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .clip(RoundedCornerShape(8.dp))
                    .aspectRatio(11f / 16f),
                painter = thumbnail,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Column(Modifier.offset(y = (-4).dp)) {
                chapters.forEach {
                    key(it.first.id) {
                        ChapterButton2(
                            chapter = it.first,
                            isRead = it.second,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShimmerFeedCard() {
    val shimmerColor = MaterialTheme.colorScheme.primary.copy(0.25f)

    Card(
        shape = RoundedCornerShape(CARD_BORDER_RADIUS),
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .shimmer()
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .fillMaxWidth()
                    .height(32.dp)
                    .background(shimmerColor)
            )
            Row(modifier = Modifier.padding(top = 8.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .fillMaxWidth(0.3f)
                        .aspectRatio(11 / 16f)
                        .background(shimmerColor)
                )
                Column(
                    modifier = Modifier
                        .weight(1f, fill = true)
                        .padding(start = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(shimmerColor)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(shimmerColor)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    DefaultPreview {
        Column {
            ChapterFeedCard(
                manga = StubData.MANGA,
                chapters = StubData.FEED_MAP_CHAPTERS,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewShimmer() {
    DefaultPreview {
        Column {
            ShimmerFeedCard()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewList() {
    DefaultPreview {
        ChapterFeed(
            chapterFeedState = ChapterFeedState(
                loading = false,
                chapterFeedItems = StubData.FEED_MAP
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewListLoading() {
    DefaultPreview {
        ChapterFeed(
            chapterFeedState = ChapterFeedState(
                loading = true,
                chapterFeedItems = emptyMap()
            ),
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewListDark() {
    DefaultPreview {
        ChapterFeed(
            chapterFeedState = ChapterFeedState(
                loading = false,
                chapterFeedItems = StubData.FEED_MAP
            ),
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewListDarkLoading() {
    DefaultPreview {
        ChapterFeed(
            chapterFeedState = ChapterFeedState(
                loading = true,
                chapterFeedItems = emptyMap()
            ),
        )
    }
}