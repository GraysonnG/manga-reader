package com.blanktheevil.mangareader.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.PreviewDataFactory
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.helpers.getCoverImageUrl
import com.blanktheevil.mangareader.helpers.title
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.ui.theme.Purple40
import com.blanktheevil.mangareader.ui.theme.Typography

@Composable
fun ChapterFeed(
    title: String,
    chapterList: List<ChapterDto>,
    mangaList: List<MangaDto>,
    navigateToReader: (String) -> Unit,
) {
    val chapterFeedData = mangaList.associateWith { manga ->
        chapterList.filter { chapter ->
            chapter.relationships.firstOrNull { rel -> rel.type == "manga" }
                ?.id == manga.id
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = title, style = Typography.headlineMedium)

        Divider(
            thickness = 2.dp,
            color = Purple40
        )

        chapterFeedData.map { (manga, chapters) ->
            ChapterFeedCard(
                manga = manga,
                chapters = chapters,
                navigateToReader = navigateToReader,
            )
        }
    }
}

@Composable
fun ChapterFeedCard(
    manga: MangaDto,
    chapters: List<ChapterDto>,
    navigateToReader: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = manga.title)
            Divider(
                color = Color.Gray.copy(alpha = 0.5f)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                manga.getCoverImageUrl()?.let {
                    ImageFromUrl(url = it)
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    chapters.forEach {
                        ChapterButton(
                            chapter = it,
                            navigateToReader = navigateToReader
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChapterButton(
    chapter: ChapterDto,
    navigateToReader: (String) -> Unit,
) {
    Button(
        shape = RoundedCornerShape(4.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp
        ),
        onClick = {
            navigateToReader(chapter.id)
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(weight = 1f, fill = true),
                text = chapter.title.ifEmpty { "No Data" },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = Icons.Rounded.ArrowForward,
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MangaReaderTheme {
        Column {
            ChapterFeedCard(
                manga = PreviewDataFactory.MANGA,
                chapters = PreviewDataFactory.CHAPTER_LIST
            ) {}
        }
    }
}

@Preview
@Composable
private fun Preview2() {
    MangaReaderTheme {
        Column {
            ChapterButton(chapter = PreviewDataFactory.CHAPTER) {}
        }
    }
}