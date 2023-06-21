package com.blanktheevil.mangareader.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.PreviewDataFactory
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.helpers.getCoverImageUrl
import com.blanktheevil.mangareader.helpers.title
import com.blanktheevil.mangareader.ui.components.ImageFromUrl
import com.blanktheevil.mangareader.ui.theme.MangaReaderDefaults
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.viewmodels.LibraryViewModel

@Composable
fun LibraryScreen(
    libraryViewModel: LibraryViewModel = viewModel(),
    navigateToMangaDetailScreen: (id: String) -> Unit,
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val uiState = libraryViewModel.uiState.collectAsState()

    OnMount {
        libraryViewModel.initViewModel(context = context)
    }
    
    Text(text = uiState.value.maxPages.toString())

    LibraryScreenLayout(
        followedMangaList = uiState.value.followedMangaList,
        navigateToMangaDetailScreen = navigateToMangaDetailScreen,
        navigateBack = navigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryScreenLayout(
    followedMangaList: List<MangaDto>,
    navigateToMangaDetailScreen: (id: String) -> Unit,
    navigateBack: () -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(
            title = { Text(text = stringResource(id = R.string.library_screen_title)) },
            colors = MangaReaderDefaults.topAppBarColors(),
            navigationIcon = { MangaReaderDefaults.BackArrowIconButton {
                navigateBack()
            }}
        ) }
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 8.dp)
                .padding(bottom = 8.dp),
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(2) { Spacer(modifier = Modifier) }
            items(followedMangaList) {
                LibraryScreenCard(
                    manga = it,
                    navigateToMangaDetailScreen = navigateToMangaDetailScreen,
                )
            }
        }
    }
}

@Composable
private fun LibraryScreenCard(
    manga: MangaDto,
    navigateToMangaDetailScreen: (id: String) -> Unit,
) {
    Card(
        modifier = Modifier.clickable(role = Role.Button) {
            navigateToMangaDetailScreen(manga.id)
        }
    ) {
        manga.getCoverImageUrl()?.let {url ->
            ImageFromUrl(
                url = url,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(11f / 16f)
            )
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(11f / 16f)
            ) {

            }
        }

        Text(
            text = manga.title,
            maxLines = 2,
            minLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LayoutPreview() {
    MangaReaderTheme {
        LibraryScreenLayout(
            followedMangaList = PreviewDataFactory.MANGA_LIST + PreviewDataFactory.MANGA_LIST,
            navigateToMangaDetailScreen = {}
        ) {}
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun LayoutPreviewDark() {
    MangaReaderTheme {
        LibraryScreenLayout(
            followedMangaList = PreviewDataFactory.MANGA_LIST + PreviewDataFactory.MANGA_LIST,
            navigateToMangaDetailScreen = {}
        ) {}
    }
}

@Preview
@Composable
private fun CardPreview() {
    MangaReaderTheme {
        LibraryScreenCard(
            manga = PreviewDataFactory.MANGA,
            navigateToMangaDetailScreen = {}
        )
    }
}
