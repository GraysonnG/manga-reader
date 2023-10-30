package com.blanktheevil.mangareader.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.LocalNavController
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.helpers.getCoverImageUrl
import com.blanktheevil.mangareader.navigation.navigateToMangaDetailScreen
import com.blanktheevil.mangareader.ui.theme.Typography
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.Dispatchers

private val CARD_BORDER_RADIUS = 4.dp

@Composable
fun MangaShelf(
    title: String,
    list: List<MangaDto>,
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
        Spacer(modifier = modifier.height(8.dp))
        Divider(
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = modifier.height(8.dp))
        LazyRow(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (
                loading
            ) {
                items(2) {
                    EmptyMangaDrawerCard()
                }
            } else {
                items(list) {
                    MangaDrawerCard(it)
                }
            }
        }
    }
}

@Composable
private fun EmptyMangaDrawerCard() {
    Card(
        shape = RoundedCornerShape(CARD_BORDER_RADIUS),
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
    manga: MangaDto,
) {
    val context = LocalContext.current
    val title = manga.attributes.title["en"]
    val image = rememberAsyncImagePainter(
        model =
        ImageRequest.Builder(context)
            .dispatcher(Dispatchers.IO)
            .data(manga.getCoverImageUrl())
            .crossfade(true)
            .build()
    )
    val navController = LocalNavController.current

    Card(
        shape = RoundedCornerShape(CARD_BORDER_RADIUS),
        modifier = Modifier
            .clip(RoundedCornerShape(CARD_BORDER_RADIUS))
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
        Column(
            modifier = Modifier
                .requiredWidth(256.dp)
        ) {
            Image(
                modifier = Modifier.aspectRatio(11f / 16f),
                painter = image,
                contentDescription = "",
                contentScale = ContentScale.Crop,
            )

            title?.let {
                Text(
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
                    text = it,
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
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
                StubData.MANGA_LIST,
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