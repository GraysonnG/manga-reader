package com.blanktheevil.mangareader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.PreviewDataFactory
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.ui.theme.Typography

@Composable
fun MangaDrawer(
    title: String,
    list: List<MangaDto>,
    modifier: Modifier = Modifier,
    onCardClicked: (id: String) -> Unit
) {
    Column {
        Text(text = title, style = Typography.headlineMedium)
        LazyRow(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(list) {
                MangaDrawerCard(it, onCardClicked)
            }
        }
    }
}

@Composable
fun MangaDrawerCard(
    manga: MangaDto,
    onCardClicked: (id: String) -> Unit,
) {
    val fileName = manga.relationships.firstOrNull {
        it.type == "cover_art"
    }?.attributes?.fileName
    val coverUrl = fileName?.let { "https://uploads.mangadex.org/covers/${manga.id}/$fileName.256.jpg" }
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
            coverUrl?.let {
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

@Preview(showBackground = true)
@Composable
private fun Preview() {
    MangaReaderTheme {
        Box(
            modifier = Modifier.padding(8.dp)
        ) {
            MangaDrawer(
                title = "The Title",
                PreviewDataFactory.MANGA_LIST
            ) {}
        }
    }
}