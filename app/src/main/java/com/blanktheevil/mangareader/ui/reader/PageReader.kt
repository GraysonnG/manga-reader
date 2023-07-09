package com.blanktheevil.mangareader.ui.reader

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
import coil.compose.AsyncImage
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
    if (pageUrls.isNotEmpty()) {
        val nextPage = min(currentPage + 1, pageUrls.size - 1)
        if (nextPage != currentPage) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0f),
                model = pageUrls[nextPage],
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
        }
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = pageUrls[currentPage],
            contentDescription = null,
            contentScale = ContentScale.Fit
        )
    }
}


@Composable
private fun ReaderUI(
    currentPage: Int,
    maxPages: Int,
    nextButtonClicked: () -> Unit,
    prevPage: () -> Unit,
    middleButtonClicked: () -> Unit,
) {
    val progress = currentPage.toFloat().plus(1f) / max(1f, maxPages.toFloat())

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

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.Transparent,
            )
        }
    }
}
