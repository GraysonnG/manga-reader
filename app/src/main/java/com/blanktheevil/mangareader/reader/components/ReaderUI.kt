package com.blanktheevil.mangareader.reader.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.Chapter
import com.blanktheevil.mangareader.data.Manga
import com.blanktheevil.mangareader.data.ScanlationGroup
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.toChapter
import com.blanktheevil.mangareader.data.toManga
import com.blanktheevil.mangareader.reader.ReaderTheme
import com.blanktheevil.mangareader.ui.components.GroupButton
import com.blanktheevil.mangareader.ui.components.groupButtonColors
import org.koin.compose.koinInject

@Composable
fun ReaderUI(
    modifier: Modifier = Modifier,
    showDetail: Boolean,
    manga: Manga,
    currentChapter: Chapter,
    scanlationGroup: ScanlationGroup?,
    onInfoButtonClicked: () -> Unit,
    closeReader: () -> Unit,
    goToNextChapter: () -> Unit,
    goToPrevChapter: () -> Unit
) = Box(modifier = modifier) {
    ReaderHeader(
        showDetail = showDetail,
        manga = manga,
        onInfoButtonClicked = onInfoButtonClicked,
        closeReader = closeReader,
    )

    ReaderNavigation(
        showDetail = showDetail,
        currentChapter = currentChapter,
        scanlationGroup = scanlationGroup,
        goToNextChapter = goToNextChapter,
        goToPrevChapter = goToPrevChapter,
    )
}

@Composable
private fun BoxScope.ReaderHeader(
    showDetail: Boolean,
    manga: Manga,
    onInfoButtonClicked: () -> Unit,
    closeReader: () -> Unit,
) = AnimatedVisibility(
    modifier = Modifier.align(Alignment.TopCenter),
    visible = showDetail,
    enter = slideInVertically { -it },
    exit = slideOutVertically { -it },
) {
    Row(
        Modifier
            .background(color = Color.Black.copy(0.8f))
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = closeReader,
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = null,
                tint = Color.White,
            )
        }
        Text(
            modifier = Modifier
                .padding(8.dp)
                .weight(1f, fill = true)
                .clickable(onClick = closeReader),
            text = manga.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color.White,
            textAlign = TextAlign.Center,
        )
        IconButton(onClick = onInfoButtonClicked) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = Color.White,
            )
        }
    }
}

@Composable
private fun BoxScope.ReaderNavigation(
    showDetail: Boolean,
    currentChapter: Chapter,
    scanlationGroup: ScanlationGroup?,
    goToNextChapter: () -> Unit,
    goToPrevChapter: () -> Unit,
) = AnimatedVisibility(
    modifier = Modifier.align(Alignment.BottomCenter),
    visible = showDetail,
    enter = slideInVertically { it },
    exit = slideOutVertically { it },
) {
    val leftChevron = painterResource(id = R.drawable.round_chevron_left_24)
    val rightChevron = painterResource(id = R.drawable.round_chevron_right_24)

    Row(
        modifier = Modifier
            .background(color = Color.Black.copy(0.8f))
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = { goToPrevChapter() },
        ) {
            Icon(painter = leftChevron, contentDescription = null, tint = Color.White)
        }

        Column(
            Modifier
                .height(IntrinsicSize.Min)
                .weight(1f, fill = true),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = currentChapter.title,
                modifier = Modifier,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            scanlationGroup?.let {
                GroupButton(
                    group = it,
                    colors = groupButtonColors(
                        contentColor = Color.White,
                    )
                )
            }
        }

        IconButton(
            onClick = { goToNextChapter() },
        ) {
            Icon(painter = rightChevron, contentDescription = null, tint = Color.White)
        }
    }
}

@Preview
@Composable
fun PreviewUI() {
    ReaderTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            ReaderHeader(
                showDetail = true,
                manga = StubData.MANGA.toManga(),
                onInfoButtonClicked = { },
                closeReader = { },
            )

            ReaderNavigation(
                showDetail = true,
                currentChapter = StubData.CHAPTER.toChapter(
                    moshi = koinInject()
                ),
                scanlationGroup = ScanlationGroup(
                    id = "abc123",
                    name = "Scanlation Group",
                    website = "https://example.com"
                ),
                goToNextChapter = { },
                goToPrevChapter = { },
            )
        }
    }
}