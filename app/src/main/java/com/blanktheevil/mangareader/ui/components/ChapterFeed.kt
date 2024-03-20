package com.blanktheevil.mangareader.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.LocalNavController
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.Manga
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.dto.utils.ChapterList
import com.blanktheevil.mangareader.data.dto.utils.chapter.toChapterList
import com.blanktheevil.mangareader.data.dto.utils.manga.toManga
import com.blanktheevil.mangareader.domain.ChapterFeedState
import com.blanktheevil.mangareader.helpers.toAsyncPainterImage
import com.blanktheevil.mangareader.navigation.navigateToMangaDetailScreen
import com.blanktheevil.mangareader.ui.RoundedCornerSmall
import com.blanktheevil.mangareader.ui.RoundedCornerXSmall
import com.blanktheevil.mangareader.ui.SpacerSmall
import com.blanktheevil.mangareader.ui.SpacerXSmall
import com.blanktheevil.mangareader.ui.smallDp
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
        AnimatedContent(
            targetState = chapterFeedState.loading,
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(300, easing = EaseIn),
                ).togetherWith(
                    fadeOut(
                        animationSpec = tween(300, easing = EaseIn),
                    )
                )
            }, label = ""
        ) { targetState ->
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (targetState) {
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
                    ChapterButton(
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shimmer()
                .heightIn(min = 250.dp)
                .height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .background(shimmerColor)
                    .fillMaxWidth(0.33f)
                    .fillMaxHeight()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(smallDp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerXSmall)
                        .background(shimmerColor)
                        .fillMaxWidth()
                        .height(24.dp)
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurface
                )

                List(2) { }.forEach {
                    Column {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerXSmall)
                                .background(shimmerColor)
                                .fillMaxWidth()
                                .height(36.dp)
                        )

                        SpacerXSmall()

                        Row(
                            modifier = Modifier.offset(x = 8.dp),
                        ) {
                            Icon(
                                modifier = Modifier.size(16.dp),
                                painter = painterResource(id = R.drawable.round_subdirectory_arrow_right_24),
                                contentDescription = null,
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerXSmall)
                                    .background(shimmerColor)
                                    .fillMaxWidth(0.5f)
                                    .height(16.dp)
                            )
                        }
                    }

                    SpacerSmall()
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
                manga = StubData.Data.MANGA.toManga(),
                chapters = StubData.Data.CHAPTER_LIST.toChapterList(),
            )
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true)
@Composable
private fun PreviewShimmer() {
    DefaultPreview {
        Surface {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                ShimmerFeedCard()
            }
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true)
@Composable
private fun PreviewList() {
    DefaultPreview {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ChapterFeed(
                chapterFeedState = ChapterFeedState(
                    loading = false,
                    chapterFeedItems = StubData.Data.FEED_MAP
                ),
            )
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true)
@Composable
private fun PreviewListLoading() {
    DefaultPreview {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ChapterFeed(
                chapterFeedState = ChapterFeedState(
                    loading = true,
                    chapterFeedItems = emptyMap()
                ),
            )
        }
    }
}