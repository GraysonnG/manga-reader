package com.blanktheevil.mangareader.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
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
import com.blanktheevil.mangareader.ui.OnBottomReached
import com.blanktheevil.mangareader.ui.components.ImageFromUrl
import com.blanktheevil.mangareader.ui.components.MangaReaderTopAppBarState
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.viewmodels.LibraryViewModel

enum class LibraryType(
    @StringRes private val titleResId: Int,
) {
    FOLLOWS(R.string.library_title_follows),
    POPULAR(R.string.library_title_popular),
    ;

    @Composable
    fun getTitle() = stringResource(titleResId)
    companion object {
        fun fromString(string: String?): LibraryType {
            return when (string?.lowercase()) {
                "follows" -> FOLLOWS
                "popular" -> POPULAR
                else -> throw IllegalArgumentException("Invalid library type: $string")
            }
        }
    }
}
@Composable
fun LibraryScreen(
    libraryViewModel: LibraryViewModel = viewModel(),
    libraryType: LibraryType,
    setTopAppBarState: (MangaReaderTopAppBarState) -> Unit,
    navigateToMangaDetailScreen: (id: String) -> Unit,
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val uiState = libraryViewModel.uiState.collectAsState()

    OnMount {
        libraryViewModel.initViewModel(context = context, libraryType)
    }

    setTopAppBarState(MangaReaderTopAppBarState(
        title = libraryType.getTitle(),
        navigateBack = navigateBack,
    ))

    LibraryScreenLayout(
        followedMangaList = uiState.value.followedMangaList,
        followedMangaLoading = uiState.value.followedMangaLoading,
        loadNextPage = libraryViewModel::loadNextPage,
        navigateToMangaDetailScreen = navigateToMangaDetailScreen,
    )
}

@Composable
private fun LibraryScreenLayout(
    followedMangaList: List<MangaDto>,
    followedMangaLoading: Boolean,
    loadNextPage: () -> Unit,
    navigateToMangaDetailScreen: (id: String) -> Unit,
) {
    val listState = rememberLazyGridState()

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(bottom = 8.dp),
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            state = listState,
        ) {
            item(span = { GridItemSpan(2) }) { Spacer(modifier = Modifier) }
            items(followedMangaList, key = { it.id }) {
                LibraryScreenCard(
                    manga = it,
                    navigateToMangaDetailScreen = navigateToMangaDetailScreen,
                )
            }

            item(span = { GridItemSpan(2) }) {
                if (followedMangaLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        listState.OnBottomReached {
            loadNextPage()
        }
    }
}

@Composable
private fun LibraryScreenCard(
    modifier: Modifier = Modifier,
    manga: MangaDto,
    navigateToMangaDetailScreen: (id: String) -> Unit,
) {
    Card(
        modifier = modifier.clickable(role = Role.Button) {
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
            modifier = Modifier.padding(8.dp),
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
            followedMangaList = PreviewDataFactory.MANGA_LIST,
            followedMangaLoading = false,
            loadNextPage = {},
            navigateToMangaDetailScreen = {},
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun LayoutPreviewDark() {
    MangaReaderTheme {
        Surface {
            LibraryScreenLayout(
                followedMangaList = PreviewDataFactory.MANGA_LIST,
                followedMangaLoading = true,
                loadNextPage = {},
                navigateToMangaDetailScreen = {}
            )
        }
    }
}

@Preview
@Composable
private fun CardPreview() {
    MangaReaderTheme {
        Box {
            LibraryScreenCard(
                manga = PreviewDataFactory.MANGA,
                navigateToMangaDetailScreen = {}
            )
        }
    }
}
