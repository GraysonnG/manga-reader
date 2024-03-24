package com.blanktheevil.mangareader.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.LocalNavController
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.data.Manga
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.dto.utils.MangaList
import com.blanktheevil.mangareader.data.dto.utils.manga.toMangaList
import com.blanktheevil.mangareader.helpers.toAsyncPainterImage
import com.blanktheevil.mangareader.navigation.navigateToMangaDetailScreen
import com.blanktheevil.mangareader.ui.RoundedCornerMedium
import com.blanktheevil.mangareader.ui.RoundedCornerSmall
import com.blanktheevil.mangareader.ui.SpacerSmall
import com.blanktheevil.mangareader.ui.largeDp
import com.blanktheevil.mangareader.ui.mediumDp
import com.blanktheevil.mangareader.ui.smallDp
import com.blanktheevil.mangareader.ui.smallPadding
import com.blanktheevil.mangareader.ui.xLarge
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeatureCarousel(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    mangaList: MangaList,
    isLoading: Boolean,
) {
    val configuration = LocalConfiguration.current
    val height by remember {
        mutableStateOf(configuration.screenHeightDp.dp * 0.8f)
    }

    if (isLoading) {
        FeatureCarouselShimmer(
            height = height
        )
    } else {
        val pagerState = rememberPagerState(
            initialPage = min(1, mangaList.size - 1),
            pageCount = { mangaList.size }
        )

        Column(modifier.height(height)) {
            title()

            HorizontalPager(
                state = pagerState,
                key = { mangaList[it].id },
                pageSpacing = smallDp,
                contentPadding = PaddingValues(
                    vertical = smallDp,
                    horizontal = largeDp,
                ),
            ) {
                val manga by remember { mutableStateOf(mangaList[it]) }
                val offset = pagerState.getOffsetFractionForPage(it)
                    .absoluteValue
                    .coerceIn(0f, 1f)

                FeatureCarouselCard(
                    manga = manga,
                    height = height - (xLarge * offset).dp,
                    alpha = max(1 - offset, 0.5f),
                )
            }
        }
    }
}

@Composable
private fun FeatureCarouselShimmer(
    height: Dp,
) {
    val shimmerColor = MaterialTheme.colorScheme.primary.copy(1f)
    val pagerState = rememberPagerState(
        pageCount = { 3 },
        initialPage = 1
    )

    HorizontalPager(
        modifier = Modifier.shimmer(),
        pageSpacing = smallDp,
        contentPadding = PaddingValues(
            vertical = smallDp,
            horizontal = largeDp
        ),
        outOfBoundsPageCount = 2,
        state = pagerState,
        userScrollEnabled = false,
    ) {
        val offset = pagerState.getOffsetFractionForPage(it)
            .absoluteValue
            .coerceIn(0f, 1f)

        Box(
            modifier = Modifier
                .clip(RoundedCornerMedium)
                .background(shimmerColor)
                .fillMaxWidth()
                .height(height - (xLarge * offset).dp)
        )
    }

}

@Composable
private fun FeatureCarouselCard(
    manga: Manga,
    height: Dp,
    alpha: Float,
) {
    val navController = LocalNavController.current
    val image = manga.coverArt
        .toAsyncPainterImage(
            crossfade = 600
        )

    var loaded by remember { mutableStateOf(false) }

    val fade by animateFloatAsState(
        targetValue = if (loaded) 1f else 0f,
        label = "fade",
        animationSpec = tween(600)
    )

    OnMount {
        delay(1)
        loaded = true
    }

    Box(
        Modifier
            .clip(RoundedCornerMedium)
            .alpha(alpha)
            .background(halfBlackToBlack)
            .widthIn(max = 600.dp)
            .fillMaxWidth()
            .height(height)
            .clickable {
                navController.navigateToMangaDetailScreen(manga.id)
            }
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(4.dp)),
            painter = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier = Modifier
                .padding(smallDp)
                .align(Alignment.BottomCenter)
                .height(IntrinsicSize.Min)
                .clip(RoundedCornerSmall)
                .alpha(fade)
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .blur(20.dp, BlurredEdgeTreatment.Rectangle),
                painter = image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alignment = Alignment.BottomCenter
            )

            Column(
                modifier = Modifier
                    .background(color = Color.Black.copy(alpha = 0.3f))
                    .smallPadding()
            ) {
                Text(
                    color = Color.White,
                    modifier = Modifier.padding(bottom = mediumDp),
                    text = manga.title,
                    maxLines = 2,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        lineHeight = 22.sp,
                    )
                )
                Text(
                    color = Color.White,
                    modifier = Modifier.padding(bottom = mediumDp),
                    text = manga.description,
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    lineHeight = 26.sp,
                )

                SpacerSmall()
            }
        }
    }
}

private val halfBlackToBlack = Brush.linearGradient(
    colors = listOf(
        Color.Black.copy(alpha = 0.5f),
        Color.Black
    ),
    start = Offset.Zero,
    end = Offset.Infinite.copy(x = 0f),
)

@PreviewLightDark
@Composable
private fun FeatureCarouselPreview() {
    DefaultPreview {
        Surface {
            Box(Modifier.fillMaxSize()) {
                FeatureCarousel(
                    isLoading = false,
                    title = {
                        Text("Test List")
                    },
                    mangaList = StubData.Data.MANGA_LIST.toMangaList(),
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun FeatureCarouselShimmerPreview() {
    DefaultPreview {
        Surface {
            Box(Modifier.fillMaxSize()) {
                FeatureCarousel(
                    isLoading = true,
                    title = {
                        Text("Test List")
                    },
                    mangaList = StubData.Data.MANGA_LIST.toMangaList(),
                )
            }
        }
    }
}