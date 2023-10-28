package com.blanktheevil.mangareader.reader.components

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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import coil.compose.AsyncImagePainter
import com.blanktheevil.mangareader.helpers.toAsyncPainterImage
import kotlin.math.max

@Composable
fun PageReaderV2(
    currentPage: Int,
    maxPages: Int,
    pageUrls: List<String>,
    nextButtonClicked: () -> Unit,
    prevButtonClicked: () -> Unit,
    middleButtonClicked: () -> Unit,
) {
    Pages(
        currentPage = currentPage,
        pageUrls = pageUrls
    )

    ReaderUI(
        currentPage = currentPage,
        maxPages = maxPages,
        nextButtonClicked = nextButtonClicked,
        prevButtonClicked = prevButtonClicked,
        middleButtonClicked = middleButtonClicked,
    )
}

@Composable
private fun Pages(currentPage: Int, pageUrls: List<String>) {
    val previous = if (currentPage > 0) getPainter(url = pageUrls[currentPage - 1]) else null
    val current = getPainter(url = pageUrls[currentPage])
    val next =
        if (currentPage < pageUrls.size) getPainter(url = pageUrls[currentPage + 1]) else null


    Page(previous, preload = true)
    Page(next, preload = true)
    Page(current)
}

@Composable
private fun getPainter(url: String): AsyncImagePainter {
    return url.toAsyncPainterImage()
}

@Composable
private fun Page(painter: Painter?, preload: Boolean = false) {
    painter?.let {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (preload) 0f else 1f),
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun ReaderUI(
    currentPage: Int,
    maxPages: Int,
    nextButtonClicked: () -> Unit,
    prevButtonClicked: () -> Unit,
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
                        prevButtonClicked()
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