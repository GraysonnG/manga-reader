@file:OptIn(ExperimentalFoundationApi::class)

package com.blanktheevil.mangareader.ui.components

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.blanktheevil.mangareader.PreviewDataFactory
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.helpers.description
import com.blanktheevil.mangareader.helpers.getCoverImageUrl
import com.blanktheevil.mangareader.helpers.tags
import com.blanktheevil.mangareader.helpers.title
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme

@Composable
fun FeatureCarousel(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    mangaList: List<MangaDto>,
    onItemClicked: (mangaId: String) -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier
            .padding(bottom = 8.dp)
    ) {
        title()
        HorizontalPager(
            pageCount = mangaList.size,
            state = rememberPagerState(),
            key = { mangaList[it].id }
        ) {
            val manga by remember { mutableStateOf(mangaList[it]) }

            FeatureCarouselCard(
                context = context,
                manga = manga,
                onItemClicked = onItemClicked,
            )
        }
    }
}

@Composable
private fun FeatureCarouselCard(
    context: Context,
    manga: MangaDto,
    onItemClicked: (mangaId: String) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val image = rememberAsyncImagePainter(
        model =
        ImageRequest.Builder(context)
            .data(manga.getCoverImageUrl())
            .crossfade(true)
            .build()
    )

    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .height(configuration.screenHeightDp.dp * 0.33f),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(Modifier.fillMaxSize().clickable {
            onItemClicked(manga.id)
        }) {
            Image(
                modifier = Modifier
                    .padding(8.dp)
                    .aspectRatio(9f / 16f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp)),
                painter = image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f, fill = true)
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text = manga.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 18.sp,
                    )
                )
                Text(
                    text = manga.description,
                    maxLines = 7,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                )

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    manga.tags.take(4).forEach {
                        AssistChip(
                            interactionSource = MutableInteractionSource(),
                            label = { Text(text = it) },
                            onClick = { })
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun FeatureCarouselPreview() {
    MangaReaderTheme {
        Surface {
            Box(Modifier.fillMaxSize()) {
                FeatureCarousel(
                    title = {
                        Text("Test List")
                    },
                    mangaList = PreviewDataFactory.MANGA_LIST,
                ) {}
            }
        }
    }
}