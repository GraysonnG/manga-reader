package com.blanktheevil.mangareader.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toLowerCase
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
import com.blanktheevil.mangareader.ui.theme.MangaReaderDefaults
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
            return when (string?.toLowerCase()) {
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
    setTopAppBar: (@Composable () -> Unit) -> Unit,
    navigateToMangaDetailScreen: (id: String) -> Unit,
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val uiState = libraryViewModel.uiState.collectAsState()

    OnMount {
        libraryViewModel.initViewModel(context = context, libraryType)
    }

    LibraryScreenLayout(
        followedMangaList = uiState.value.followedMangaList,
        followedMangaLoading = uiState.value.followedMangaLoading,
        libraryType = libraryType,
        loadNextPage = libraryViewModel::loadNextPage,
        setTopAppBar = setTopAppBar,
        navigateToMangaDetailScreen = navigateToMangaDetailScreen,
        navigateBack = navigateBack,
    )
}

@Composable
private fun LibraryScreenLayout(
    followedMangaList: List<MangaDto>,
    followedMangaLoading: Boolean,
    libraryType: LibraryType,
    loadNextPage: () -> Unit,
    setTopAppBar: (@Composable () -> Unit) -> Unit,
    navigateToMangaDetailScreen: (id: String) -> Unit,
    navigateBack: () -> Unit,
) {
    setTopAppBar {
        LibraryScreenTopAppBar(
            title = libraryType.getTitle(),
            navigateBack = navigateBack,
        )
    }

    val listState = rememberLazyGridState()

    Column {
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
            text = manga.title,
            maxLines = 2,
            minLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryScreenTopAppBar(
    title: String,
    navigateBack: () -> Unit,
) {
    TopAppBar(
        title = { Text(text = title) },
        actions = {
            DropdownMenu(expanded = false, onDismissRequest = { /*TODO*/ }) {
                DropdownMenuItem(text = { Text(text = "hello") }, onClick = { /*TODO*/ })
            }
        },
        colors = MangaReaderDefaults.topAppBarColors(),
        navigationIcon = { MangaReaderDefaults.BackArrowIconButton {
            navigateBack()
        }}
    )
}

@Preview(showBackground = true)
@Composable
private fun LayoutPreview() {
    MangaReaderTheme {
        LibraryScreenLayout(
            followedMangaList = PreviewDataFactory.MANGA_LIST,
            followedMangaLoading = false,
            loadNextPage = {},
            setTopAppBar = {},
            navigateToMangaDetailScreen = {},
            libraryType = LibraryType.FOLLOWS,
        ) {}
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
                setTopAppBar = {},
                libraryType = LibraryType.FOLLOWS,
                navigateToMangaDetailScreen = {}
            ) {}
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
