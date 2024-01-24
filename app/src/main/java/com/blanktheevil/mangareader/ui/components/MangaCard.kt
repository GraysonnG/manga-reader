package com.blanktheevil.mangareader.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.LocalNavController
import com.blanktheevil.mangareader.data.Manga
import com.blanktheevil.mangareader.navigation.navigateToMangaDetailScreen

@Composable
fun MangaCard(
    modifier: Modifier = Modifier,
    manga: Manga,
) {
    val navController = LocalNavController.current

    Card(
        modifier = modifier.clickable(role = Role.Button) {
            navController.navigateToMangaDetailScreen(manga.id)
        }
    ) {
        manga.coverArt?.let { url ->
            ImageFromUrl(
                url = url,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(11f / 16f)
            )
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(11f / 16f)
            ) {

            }
        }

        Text(
            modifier = Modifier.padding(8.dp),
            text = manga.title,
            maxLines = 2,
            minLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}