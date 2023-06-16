package com.blanktheevil.mangareader.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.ui.components.ImageFromUrl
import com.blanktheevil.mangareader.ui.components.MangaDrawer
import com.blanktheevil.mangareader.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    navigateToLogin: () -> Unit,
    navigateToMangaDetail: (id: String) -> Unit,
) {
    val context = LocalContext.current
    val uiState by homeViewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        homeViewModel.initViewModel(context = context)
        homeViewModel.getMangaList()
    }

    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Button(onClick = {
            homeViewModel.logout()
        }) {
            Text("Logout")
        }

        MangaDrawer(
            title = stringResource(id = R.string.home_page_drawer_follows),
            list = uiState.mangaList,
            onCardClicked = navigateToMangaDetail
        )
    }
}

@Composable
fun MangaList(mangaList: List<MangaDto>) {
    LazyColumn {
        items(mangaList) { mangaDto ->
            Text(text = mangaDto.attributes.title["en"] ?: "could not find title")
        }
    }
}