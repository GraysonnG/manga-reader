package com.blanktheevil.mangareader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
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
import com.blanktheevil.mangareader.helpers.getCoverImageUrl
import com.blanktheevil.mangareader.helpers.title
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.ui.theme.Purple40

@Composable
fun MangaList(
    manga: List<MangaDto>,
    navigateToMangaDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        manga.forEach {
            MangaCard(it, navigateToMangaDetail)
        }
    }
}

@Composable
private fun MangaCard(
    manga: MangaDto,
    navigateToMangaDetail: (String) -> Unit,
) {
    Card(
        Modifier.clickable(
            role = Role.Button
        ) {
            navigateToMangaDetail(manga.id)
        }
    ) {
        Row() {
            manga.getCoverImageUrl()?.let {
                ImageFromUrl(
                    modifier = Modifier
                        .background(Purple40.copy(alpha = 0.5f))
                        .height(100.dp)
                        .aspectRatio(11f / 16f),
                    url = it
                )
            }
            Column(
                modifier = Modifier.padding(
                    start = 8.dp,
                    end = 4.dp,
                    top = 4.dp,
                    bottom = 4.dp,
                )
            ) {
                Text(
                    text = manga.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                )
                Divider()
                Text(
                    text = manga.attributes.description["en"] ?: "",
                    maxLines = 3,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MangaReaderTheme {
        MangaList(
            manga = PreviewDataFactory.MANGA_LIST,
            navigateToMangaDetail = {}
        )
    }
}