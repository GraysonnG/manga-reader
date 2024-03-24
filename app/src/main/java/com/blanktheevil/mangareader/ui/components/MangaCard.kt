package com.blanktheevil.mangareader.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.LocalNavController
import com.blanktheevil.mangareader.data.Manga
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.dto.utils.manga.toManga
import com.blanktheevil.mangareader.helpers.toAsyncPainterImage
import com.blanktheevil.mangareader.navigation.navigateToMangaDetailScreen
import com.blanktheevil.mangareader.ui.RoundedCornerSmall
import com.blanktheevil.mangareader.ui.RoundedCornerXSmall
import com.blanktheevil.mangareader.ui.smallPadding
import com.blanktheevil.mangareader.ui.xSmallPadding
import com.valentinilk.shimmer.shimmer

@Composable
fun MangaCard(
    modifier: Modifier = Modifier,
    manga: Manga,
) {
    val navController = LocalNavController.current
    val localDensity = LocalDensity.current
    val image = manga.coverArt.toAsyncPainterImage(
        crossfade = true
    )

    var height by remember { mutableStateOf(0.dp) }

    Card(
        modifier = modifier
            .height(height)
            .clickable(role = Role.Button) {
                navController.navigateToMangaDetailScreen(manga.id)
            }
            .onSizeChanged {
                with(localDensity) {
                    height = it.width
                        .times(1.66667f)
                        .toDp()
                }
            },
        shape = RoundedCornerSmall,
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize(),
                painter = image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )

            Box(
                modifier = Modifier
                    .xSmallPadding()
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .clip(RoundedCornerXSmall)
            ) {
                var columnHeight by remember {
                    mutableStateOf(0.dp)
                }

                Image(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(columnHeight)
                        .blur(20.dp, BlurredEdgeTreatment.Rectangle),
                    painter = image,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    alignment = Alignment.BottomCenter,
                )

                Column(
                    modifier = Modifier
                        .onGloballyPositioned {
                            columnHeight = with(localDensity) { it.size.height.toDp() }
                        }
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .background(color = Color.Black.copy(alpha = 0.4f))
                        .smallPadding()
                ) {
                    Text(
                        text = manga.title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
fun MangaCardShimmer(
    modifier: Modifier = Modifier,
) {
    val shimmerColor = MaterialTheme.colorScheme.primary.copy(0.25f)
    val localDensity = LocalDensity.current
    var height by remember {
        mutableStateOf(0.dp)
    }

    Card(
        modifier = modifier
            .shimmer()
            .height(height)
            .onSizeChanged {
                with(localDensity) {
                    height = it.width
                        .times(1.66667f)
                        .toDp()
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(0.25f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .smallPadding(),
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerXSmall)
                    .align(Alignment.BottomCenter)
                    .height(32.dp)
                    .fillMaxWidth()
                    .background(shimmerColor)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun MangaCardPreview() {
    DefaultPreview {
        MangaCard(
            manga = StubData.Data.MANGA.toManga()
        )
    }
}

@PreviewLightDark
@Composable
private fun MangaCardShimmerPreview() {
    DefaultPreview {
        Surface {
            MangaCardShimmer()
        }
    }
}