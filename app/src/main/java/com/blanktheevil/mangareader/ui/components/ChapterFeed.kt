package com.blanktheevil.mangareader.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
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
    readChapterIds: List<String>,
    navigateToReader: (String, String) -> Unit,
    navigateToMangaDetail: (String) -> Unit,
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
                navigateToMangaDetail = navigateToMangaDetail,
                readChapterIds = readChapterIds,
            )
        }
    }
}

@Composable
fun ChapterFeedCard(
    manga: MangaDto,
    chapters: List<ChapterDto>,
    readChapterIds: List<String>,
    navigateToReader: (String, String) -> Unit,
    navigateToMangaDetail: (String) -> Unit,
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
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        role = Role.Button
                    ) { navigateToMangaDetail(manga.id) },
                text = manga.title
            )
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
                            mangaId = manga.id,
                            chapter = it,
                            isRead = readChapterIds.contains(it.id),
                            navigateToReader = navigateToReader
                        )
                    }
                }
            }
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
                chapters = PreviewDataFactory.CHAPTER_LIST,
                readChapterIds = emptyList(),
                navigateToReader = {_,_->},
                navigateToMangaDetail = {}
            )
        }
    }
}