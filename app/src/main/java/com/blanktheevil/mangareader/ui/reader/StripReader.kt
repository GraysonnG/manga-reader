package com.blanktheevil.mangareader.ui.reader

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.blanktheevil.mangareader.R

@Composable
fun StripReader(
    pageUrls: List<String>,
    isVertical: Boolean = true,
    onScreenClick: () -> Unit,
    nextButtonClicked: () -> Unit,
    onLastPageViewed: () -> Unit,
) {
    val context = LocalContext.current
    val scrollState = rememberLazyListState()
    val layoutInfo by remember { derivedStateOf { scrollState.layoutInfo } }
    val interactionSource by remember { mutableStateOf(MutableInteractionSource()) }

    val isAtEnd = layoutInfo.visibleItemsInfo.lastOrNull()
        ?.index == layoutInfo.totalItemsCount - 1

    LaunchedEffect(isAtEnd) {
        if (isAtEnd) onLastPageViewed()
    }

    Box(
        Modifier.fillMaxSize()
    ) {
        if (isVertical)  {
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                    ) {
                        onScreenClick()
                    }
            ) {
                readerPages(pageUrls = pageUrls, context = context, isVertical = true)
            }
        } else {
            LazyRow(
                state = scrollState,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                    ) {
                        onScreenClick()
                    }
            ) {
                readerPages(pageUrls = pageUrls, context = context, isVertical = false)
            }
        }

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = isAtEnd,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable(
                        onClick = nextButtonClicked,
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    color = MaterialTheme.colorScheme.onPrimary,
                    text = stringResource(id = R.string.reader_strip_next_button),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

private fun LazyListScope.readerPages(
    pageUrls: List<String>,
    context: Context,
    isVertical: Boolean,
) {
    items(
        pageUrls,
        key = { it },
    ) {
        ReaderPage(url = it, context = context, isVertical = isVertical)
    }

    item {
        Spacer(Modifier.size(50.dp))
    }
}

@Composable
private fun ReaderPage(url: String, context: Context, isVertical: Boolean) {
    var loading by remember { mutableStateOf(true) }
    Box(
        modifier = Modifier
            .heightIn(64.dp, Dp.Infinity)
            .widthIn(64.dp, Dp.Infinity),
    ) {
        AsyncImage(
            modifier = Modifier.then(if (isVertical) {
                Modifier.fillMaxWidth()
            } else {
                Modifier.fillMaxSize()
            }),
            model = ImageRequest.Builder(context)
                .data(url)
                .build(),
            contentDescription = null,
            onSuccess = {
                loading = false
            }
        )
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Preview
@Composable
private fun VerticalReaderPreview() {
//    VerticalReader(emptyList()) {}
}