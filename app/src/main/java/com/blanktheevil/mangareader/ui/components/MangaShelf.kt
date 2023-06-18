package com.blanktheevil.mangareader.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.PreviewDataFactory
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.helpers.getCoverImageUrl
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.ui.theme.Purple40
import com.blanktheevil.mangareader.ui.theme.Typography

@Composable
fun MangaShelf(
    title: String,
    list: List<MangaDto>,
    loading: Boolean,
    modifier: Modifier = Modifier,
    onCardClicked: (id: String) -> Unit
) {
    Column {
        Text(text = title, style = Typography.headlineMedium)
        Spacer(modifier = modifier.height(8.dp))
        Divider(
            thickness = 2.dp,
            color = Purple40
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
                    MangaDrawerCard(it, onCardClicked)
                }

                item {
                    Card(
                        modifier = Modifier
                            .height(450.dp)
                            .width(200.dp),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(50.dp),
                                imageVector = Icons.Rounded.ArrowForward,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyMangaDrawerCard() {
    Card(
        modifier = Modifier
            .requiredHeight(450.dp)
            .width(256.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Purple40,
        )
    ) {
    }
}

@Composable
fun MangaDrawerCard(
    manga: MangaDto,
    onCardClicked: (id: String) -> Unit,
) {
    val title = manga.attributes.title["en"]

    Card(
        modifier = Modifier
            .clickable(
                role = Role.Button
            ) { onCardClicked(manga.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .requiredWidth(256.dp)
        ) {
            manga.getCoverImageUrl()?.let {
                ImageFromUrl(
                    modifier = Modifier.aspectRatio(11f / 16f),
                    url = it
                )
            }

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
    MangaReaderTheme {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MangaShelf(
                title = "The Title",
                PreviewDataFactory.MANGA_LIST,
                loading = false
            ) {}
            MangaShelf(
                title = "The Title",
                emptyList(),
                loading = true
            ) {}
        }
    }
}