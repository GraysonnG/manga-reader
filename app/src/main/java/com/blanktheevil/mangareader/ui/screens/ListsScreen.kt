package com.blanktheevil.mangareader.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.MangaList
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.dto.UserListAttributesDto
import com.blanktheevil.mangareader.data.dto.UserListDto
import com.blanktheevil.mangareader.data.toMangaList
import com.blanktheevil.mangareader.ui.components.MangaReaderTopAppBarState
import com.blanktheevil.mangareader.ui.components.MangaShelf
import com.blanktheevil.mangareader.viewmodels.ListsScreenViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ListsScreen(
    listsScreenViewModel: ListsScreenViewModel = koinViewModel(),
    setTopAppBarState: (MangaReaderTopAppBarState) -> Unit,
) {
    val state by listsScreenViewModel.state.collectAsState()
    val icon = painterResource(id = R.drawable.round_list_24)

    setTopAppBarState(
        MangaReaderTopAppBarState(
            title = "Custom Lists",
            titleIcon = icon,
        )
    )

    OnMount {
        listsScreenViewModel.initViewModel()
    }

    Box(Modifier.fillMaxSize()) {
        Lists(
            items = state.lists,
            loading = state.mangaListsLoading,
        )
    }
}

@Composable
fun Lists(
    items: Map<UserListDto, MangaList>,
    loading: Boolean,
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(72.dp)
    ) {
        Spacer(Modifier)
        items.forEach { (userList, mangaList) ->
            key(userList.id) {
                List(
                    userListName = userList.attributes.name,
                    mangaList = mangaList,
                    loading = loading,
                )
            }
        }
        Spacer(Modifier)
    }
}

@Composable
fun List(
    userListName: String,
    mangaList: MangaList,
    loading: Boolean,
) {
    MangaShelf(
        title = userListName,
        list = mangaList,
        loading = loading,
    )
}

@Preview
@Composable
private fun PreviewLight() {
    DefaultPreview {
        Surface(
            Modifier.fillMaxSize()
        ) {
            Lists(
                items = mapOf(
                    UserListDto(
                        id = "1",
                        attributes = UserListAttributesDto(
                            name = "Favorites",
                            visibility = "public",
                            version = 1
                        ),
                        relationships = emptyList()
                    ) to StubData.MANGA_LIST.toMangaList(),
                    UserListDto(
                        id = "2",
                        attributes = UserListAttributesDto(
                            name = "Reading",
                            visibility = "public",
                            version = 1
                        ),
                        relationships = emptyList()
                    ) to StubData.MANGA_LIST.toMangaList(),
                    UserListDto(
                        id = "3",
                        attributes = UserListAttributesDto(
                            name = "Completed",
                            visibility = "public",
                            version = 1
                        ),
                        relationships = emptyList()
                    ) to StubData.MANGA_LIST.toMangaList(),
                ),
                loading = false,
            )
        }
    }
}