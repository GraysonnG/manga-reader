package com.blanktheevil.mangareader.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.blanktheevil.mangareader.viewmodels.ReaderViewModel
import kotlin.math.max

@Composable
fun ReaderScreen(
    chapterId: String?,
    readerViewModel: ReaderViewModel = viewModel()
) {
    val uiState by readerViewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        chapterId?.let { readerViewModel.initChapter(chapterId = chapterId, context = context) }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (uiState.pageUrls.isNotEmpty()) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = ImageRequest.Builder(context)
                    .data(uiState.pageUrls[uiState.currentPage])
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
        }

        ReaderUI(
            currentPage = uiState.currentPage,
            maxPages = uiState.maxPages,
            nextPage = readerViewModel::nextPage,
            prevPage = readerViewModel::prevPage,
        )
    }
}

@Composable
private fun ReaderUI(
    currentPage: Int,
    maxPages: Int,
    nextPage: () -> Unit,
    prevPage: () -> Unit,
) {
    val progress = currentPage.toFloat().plus(1f) / max(1f, maxPages.toFloat())

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth(),
            color = Color.White
        )
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
                .weight(1f)) {}

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable(role = Role.Button) {
                        nextPage()
                    }
            ) {}
        }
    }
}