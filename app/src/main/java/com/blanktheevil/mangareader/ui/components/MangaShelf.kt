package com.blanktheevil.mangareader.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.LocalNavController
import com.blanktheevil.mangareader.data.Manga
import com.blanktheevil.mangareader.data.MangaList
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.toMangaList
import com.blanktheevil.mangareader.helpers.toAsyncPainterImage
import com.blanktheevil.mangareader.navigation.navigateToMangaDetailScreen
import com.blanktheevil.mangareader.ui.RoundedCornerSmall
import com.blanktheevil.mangareader.ui.RoundedCornerXSmall
import com.blanktheevil.mangareader.ui.SpacerSmall
import com.blanktheevil.mangareader.ui.smallDp
import com.blanktheevil.mangareader.ui.smallPadding
import com.blanktheevil.mangareader.ui.theme.Typography
import com.blanktheevil.mangareader.ui.xSmallPadding
import com.valentinilk.shimmer.shimmer

@Composable
fun MangaShelf(
    title: String,
    list: MangaList,
    loading: Boolean,
    modifier: Modifier = Modifier,
    onTitleClicked: (() -> Unit)? = null,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = onTitleClicked != null,
                    role = Role.Button
                ) {
                    onTitleClicked?.invoke()
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = Typography.headlineMedium,
                maxLines = 1,
            )

            if (onTitleClicked != null) {
                Icon(imageVector = Icons.Rounded.ArrowForward, contentDescription = null)
            }
        }
        SpacerSmall()
        HorizontalDivider(
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.primary
        )
        SpacerSmall()
        LazyRow(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(smallDp)
        ) {
            if (
                loading
            ) {
                items(2) {
                    EmptyMangaDrawerCard()
                }
            } else {
                items(list, key = { it.id }) {
                    MangaDrawerCard(it)
                }
            }
        }
    }
}

@Composable
private fun EmptyMangaDrawerCard() {
    Card(
        shape = RoundedCornerXSmall,
        modifier = Modifier
            .requiredHeight(450.dp)
            .width(256.dp)
            .shimmer(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onSurface.copy(0.25f))
        )
    }
}

@Composable
fun MangaDrawerCard(
    manga: Manga,
) {
    val image = manga.coverArt.toAsyncPainterImage(
        crossfade = true
    )
    val navController = LocalNavController.current
    val localDensity = LocalDensity.current

    Card(
        shape = RoundedCornerSmall,
        modifier = Modifier
            .clickable(
                role = Role.Button
            ) { navController.navigateToMangaDetailScreen(manga.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults
            .cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ),
    ) {
        Box(
            modifier = Modifier
                .width(256.dp)
                .height(450.dp)
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
                        modifier = Modifier.padding(bottom = smallDp),
                        text = manga.title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 2000)
@Composable
private fun Preview() {
    DefaultPreview {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MangaShelf(
                title = "The Title",
                StubData.MANGA_LIST.toMangaList(),
                loading = false,
                onTitleClicked = {},
            )
            MangaShelf(
                title = "The Title",
                emptyList(),
                loading = true,
                onTitleClicked = {},
            )
        }
    }
}