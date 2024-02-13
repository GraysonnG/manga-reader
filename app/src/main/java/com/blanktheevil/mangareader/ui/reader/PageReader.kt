package com.blanktheevil.mangareader.ui.reader

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.dto.utils.manga.getCoverImageUrl
import com.blanktheevil.mangareader.helpers.toAsyncPainterImage
import kotlin.math.max
import kotlin.math.min

@Composable
fun PageReader(
    currentPage: Int,
    maxPages: Int,
    pageUrls: List<String>,
    nextButtonClicked: () -> Unit,
    prevButtonClicked: () -> Unit,
    middleButtonClicked: () -> Unit,
) {
    ReaderPages(
        currentPage = currentPage,
        pageUrls = pageUrls,
    )

    ReaderUI(
        currentPage = currentPage,
        maxPages = maxPages,
        nextButtonClicked = nextButtonClicked,
        prevPage = prevButtonClicked,
        middleButtonClicked = middleButtonClicked,
    )
}

@Composable
private fun ReaderPages(
    currentPage: Int,
    pageUrls: List<String>,
) {
    val currentPagePainter = pageUrls.getOrNull(currentPage)
        .toAsyncPainterImage()
    val nextPagePainter = pageUrls.getOrNull(currentPage + 1)
        .toAsyncPainterImage()

    Image(
        modifier = Modifier.alpha(0f),
        painter = nextPagePainter, contentDescription = null
    )
    Image(
        modifier = Modifier.fillMaxSize(),
        painter = currentPagePainter,
        contentDescription = null,
        contentScale = ContentScale.Fit
    )
}


@Composable
private fun ReaderUI(
    currentPage: Int,
    maxPages: Int,
    nextButtonClicked: () -> Unit,
    prevPage: () -> Unit,
    middleButtonClicked: () -> Unit,
) {

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.weight(1f, fill = true)
            ) {
                Box(modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable(role = Role.Button) {
                        prevPage()
                    }
                ) {}

                Box(modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable(role = Role.Button) {
                        middleButtonClicked()
                    }
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable(role = Role.Button) {
                            nextButtonClicked()
                        }
                ) {}
            }
        }
    }
}

@Preview()
@Composable
private fun Preview() {
    PageReader(
        currentPage = 0,
        maxPages = 2,
        pageUrls = listOf(
            StubData.Data.MANGA.getCoverImageUrl()!!,
            StubData.Data.MANGA.getCoverImageUrl()!!,
        ),
        nextButtonClicked = { },
        prevButtonClicked = { },
        middleButtonClicked = { })
}
