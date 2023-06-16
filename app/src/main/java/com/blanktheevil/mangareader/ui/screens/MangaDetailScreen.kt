package com.blanktheevil.mangareader.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blanktheevil.mangareader.PreviewDataFactory
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.helpers.title
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.ui.theme.Purple40
import com.blanktheevil.mangareader.viewmodels.MangaDetailViewModel

@Composable
fun MangaDetailScreen(
    mangaDetailViewModel: MangaDetailViewModel = viewModel(),
    id: String?,
    popBackStack: () -> Unit,
) {
    val uiState by mangaDetailViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        id?.let { mangaDetailViewModel.getMangaDetails(id) }
    }

    if (!uiState.loading) {
        uiState.data?.let {
            MangaDetailLayout(
                manga = it,
                chapters = uiState.chapters,
                popBackStack = popBackStack
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MangaDetailLayout(
    manga: MangaDto,
    chapters: List<ChapterDto>,
    popBackStack: () -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(
            title = {
                Text(
                    text = manga.attributes.title["en"] ?: "unknown",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            navigationIcon = {
                IconButton(onClick = popBackStack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Purple40,
                scrolledContainerColor = Purple40,
                navigationIconContentColor = Color.White,
                titleContentColor = Color.White,
                actionIconContentColor = Color.White
            )
        ) }
    ) { paddingValues ->
        var selectedTabIndex by rememberSaveable {
            mutableStateOf(1)
        }
        val tabs = listOf("Description", "Chapters", "More...")

        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
            ) {
                tabs.forEachIndexed { index, s ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }
                    ) {
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text=s
                        )
                    }
                }
            }

            Box(
                modifier = Modifier.padding(8.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(48.dp)
                        ) {
                            manga.attributes.description["en"]?.let {
                                Text(text = it)
                            }


                        }
                    }
                    1 -> {
                        ChapterList(list = chapters)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChapterList(list: List<ChapterDto>) {
    LazyColumn(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(list) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {  },
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    text = it.title,
                    maxLines = 1
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    MangaReaderTheme {
        MangaDetailLayout(
            manga = PreviewDataFactory.MANGA,
            chapters = PreviewDataFactory.CHAPTER_LIST
        ) {}
    }
}