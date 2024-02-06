package com.blanktheevil.mangareader.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.LocalNavController
import com.blanktheevil.mangareader.data.ChapterList
import com.blanktheevil.mangareader.data.Manga
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.toChapterList
import com.blanktheevil.mangareader.data.toManga
import com.blanktheevil.mangareader.domain.ChapterFeedState
import com.blanktheevil.mangareader.helpers.toAsyncPainterImage
import com.blanktheevil.mangareader.navigation.navigateToMangaDetailScreen
import com.blanktheevil.mangareader.ui.RoundedCornerSmall
import com.blanktheevil.mangareader.ui.RoundedCornerXSmall
import com.blanktheevil.mangareader.ui.largeDp
import com.blanktheevil.mangareader.ui.mediumDp
import com.blanktheevil.mangareader.ui.smallDp
import com.blanktheevil.mangareader.ui.smallPadding
import com.blanktheevil.mangareader.ui.smallPaddingVertical
import com.valentinilk.shimmer.shimmer


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
    manga: Manga,
    chapters: ChapterList,
) {
    val navController = LocalNavController.current

    val thumbnail = manga.coverArt
        .toAsyncPainterImage(
            crossfade = true
        )

    Card(
        shape = RoundedCornerSmall,
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 250.dp)
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
                .clip(RoundedCornerXSmall),
        ) {
            Image(
                modifier = Modifier
                    .clickable(
                        role = Role.Button
                    ) { navController.navigateToMangaDetailScreen(manga.id) }
                    .fillMaxWidth(0.33f)
                    .fillMaxHeight(),
                painter = thumbnail,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(20.dp, BlurredEdgeTreatment.Rectangle),
                    painter = thumbnail,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.BottomCenter,
                )

                ChapterFeedCardContent(manga = manga, chapters = chapters)
            }
        }
    }
}

@Composable
private fun ChapterFeedCardContent(
    manga: Manga,
    chapters: ChapterList,
) {
    val navController = LocalNavController.current

    Column(
        Modifier
            .background(Color.Black.copy(alpha = 0.3f))
            .fillMaxSize()
            .padding(smallDp)
    ) {
        CompositionLocalProvider(
            LocalContentColor provides Color.White,
        ) {
            Text(
                modifier = Modifier
                    .clickable {
                        navController.navigateToMangaDetailScreen(
                            manga.id
                        )
                    }
                    .fillMaxWidth(),
                text = manga.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            HorizontalDivider(
                modifier = Modifier.smallPaddingVertical(),
                color = Color.White
            )

            chapters.forEach {
                key(it.id) {
                    ChapterButton2(
                        chapter = it,
                    )
                }
            }
        }

    }
}

@Composable
private fun ShimmerFeedCard() {
    val shimmerColor = MaterialTheme.colorScheme.primary.copy(0.25f)

    Card(
        shape = RoundedCornerXSmall,
    ) {
        Column(
            modifier = Modifier
                .heightIn(min = 250.dp)
                .smallPadding()
                .shimmer()
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerXSmall)
                    .fillMaxWidth()
                    .height(largeDp)
                    .background(shimmerColor)
            )

            HorizontalDivider(
                modifier = Modifier.smallPaddingVertical(),
                color = Color.White
            )

            Column(
                modifier = Modifier
                    .weight(1f, fill = true)
                    .padding(start = smallDp),
                verticalArrangement = Arrangement.spacedBy(mediumDp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerXSmall)
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(shimmerColor)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerXSmall)
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(shimmerColor)
                )
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
                manga = StubData.Data.MANGA.toManga(),
                chapters = StubData.Data.CHAPTER_LIST.toChapterList(),
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
                chapterFeedItems = StubData.Data.FEED_MAP
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
                chapterFeedItems = StubData.Data.FEED_MAP
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