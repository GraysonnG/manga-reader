package com.blanktheevil.mangareader.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.helpers.getCoverImageUrl
import com.blanktheevil.mangareader.helpers.title
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
        manga.forEachIndexed { index, it ->
            MangaCard(
                index = index,
                manga = it,
                navigateToMangaDetail = navigateToMangaDetail
            )
        }
    }
}

@Composable
private fun MangaCard(
    index: Int = 0,
    manga: MangaDto,
    navigateToMangaDetail: (String) -> Unit,
) {
    var target by remember { mutableStateOf(0f) }
    val scale by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(
            delayMillis = index * 50
        )
    )

    OnMount {
        target = 1f
    }

    Card(
        Modifier
            .scale(scale)
            .clickable(
                role = Role.Button
            ) {
                navigateToMangaDetail(manga.id)
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
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
                    text = manga.attributes.description?.get("en") ?: "",
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
    DefaultPreview {
        Surface {
            MangaList(
                manga = StubData.MANGA_LIST,
                navigateToMangaDetail = {}
            )
        }
    }
}

@Preview(
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun DarkPreview() {
    DefaultPreview {
        Surface {
            MangaList(
                manga = StubData.MANGA_LIST,
                navigateToMangaDetail = {}
            )
        }
    }
}