package com.blanktheevil.mangareader.ui.reader_v2

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.dto.utils.manga.getCoverImageUrl
import com.blanktheevil.mangareader.helpers.toAsyncPainterImage

@Composable
fun PageReader(
    currentPage: Int,
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
        .toAsyncPainterImage(crossfade = false)
    val nextPagePainter = pageUrls.getOrNull(currentPage + 1)
        .toAsyncPainterImage(crossfade = false)

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
        pageUrls = listOf(
            StubData.Data.MANGA.getCoverImageUrl()!!,
            StubData.Data.MANGA.getCoverImageUrl()!!,
        ),
        nextButtonClicked = { },
        prevButtonClicked = { },
        middleButtonClicked = { })
}
