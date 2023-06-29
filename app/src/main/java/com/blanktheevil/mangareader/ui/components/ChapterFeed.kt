package com.blanktheevil.mangareader.ui.components

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.PreviewDataFactory
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.helpers.getCoverImageUrl
import com.blanktheevil.mangareader.helpers.title
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.ui.theme.Purple40
import com.blanktheevil.mangareader.ui.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun ChapterFeed(
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)? = null,
    chapterList: List<ChapterDto>,
    mangaList: List<MangaDto>,
    readChapterIds: List<String>,
    loading: Boolean,
    navigateToReader: (String, String) -> Unit,
    navigateToMangaDetail: (String) -> Unit,
    unCapped: Boolean = false,
) {
    val chapterFeedData = mangaList.associateWith { manga ->
        chapterList.filter { chapter ->
            chapter.relationships.firstOrNull { rel -> rel.type == "manga" }
                ?.id == manga.id
        }
    }
    var shouldShowMore by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        title?.let {
            title()

            Divider(
                thickness = 2.dp,
                color = Purple40
            )
        }

        if (!loading) {
            chapterFeedData.entries.take(
                if (shouldShowMore || unCapped) Int.MAX_VALUE else 3
            ).mapIndexed { index, (manga, chapters) ->
                ChapterFeedCard(
                    modifier = Modifier,
                    index = index,
                    manga = manga,
                    chapters = chapters,
                    navigateToReader = navigateToReader,
                    navigateToMangaDetail = navigateToMangaDetail,
                    readChapterIds = readChapterIds,
                )
            }

            if (!shouldShowMore && !unCapped) {
                Button(onClick = { shouldShowMore = true }) {
                    Text(text = "Show More")
                }
            }
        } else {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun ChapterFeedCard(
    modifier: Modifier = Modifier,
    index: Int,
    manga: MangaDto,
    chapters: List<ChapterDto>,
    readChapterIds: List<String>,
    navigateToReader: (String, String) -> Unit,
    navigateToMangaDetail: (String) -> Unit,
) {
    val context = LocalContext.current
    val thumbnail = rememberAsyncImagePainter(model =
        ImageRequest.Builder(context)
            .data(manga.getCoverImageUrl())
            .crossfade(true)
            .build()
        )
    var chapterData: Map<ChapterDto, Boolean> by remember { mutableStateOf(emptyMap()) }
    val transition = updateTransition(targetState = chapterData.isNotEmpty(), label = null)

    val offsetX by transition.animateDp(
        transitionSpec = { tween(delayMillis = 100 * index) },
        label = "offsetX",
    ) {
        if (it) 0.dp else 100.dp
    }

    val opacity by transition.animateFloat(
        transitionSpec = { tween(delayMillis = 100 * index) },
        label = "opacity",
    ) {
        if (it) 1f else 0f
    }

    OnMount {
        this.launch {
            chapterData = chapters.associateWith { chapter ->
                readChapterIds.contains(chapter.id)
            }
        }
    }

    Card(
        modifier = modifier
            .offset(x = offsetX)
            .alpha(opacity)
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

                Image(
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .clip(RoundedCornerShape(4.dp))
                        .aspectRatio(11f / 16f),
                    painter = thumbnail,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .offset(y = (-4).dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    chapterData.forEach { (chapter, isRead) ->
                        ChapterButton(
                            mangaId = manga.id,
                            chapter = chapter,
                            isRead = isRead,
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
                index = 0,
                manga = PreviewDataFactory.MANGA,
                chapters = PreviewDataFactory.CHAPTER_LIST,
                readChapterIds = emptyList(),
                navigateToReader = {_,_->},
                navigateToMangaDetail = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewList() {
    MangaReaderTheme {
        ChapterFeed(
            title = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Chapter Feed",
                    style = Typography.headlineMedium
                )
            },
            chapterList = PreviewDataFactory.CHAPTER_LIST,
            mangaList = PreviewDataFactory.MANGA_LIST,
            readChapterIds = emptyList(),
            loading = false,
            navigateToReader = {_,_->},
            navigateToMangaDetail = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewListNoTitle() {
    MangaReaderTheme {
        ChapterFeed(
            title = null,
            chapterList = PreviewDataFactory.CHAPTER_LIST,
            mangaList = PreviewDataFactory.MANGA_LIST,
            readChapterIds = emptyList(),
            loading = false,
            unCapped = true,
            navigateToReader = {_,_->},
            navigateToMangaDetail = {}
        )
    }
}