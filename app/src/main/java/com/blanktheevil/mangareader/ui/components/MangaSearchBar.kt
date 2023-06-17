package com.blanktheevil.mangareader.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.PreviewDataFactory
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme

@Composable
fun MangaSearchBar(
    manga: List<MangaDto>,
    value: String,
    onValueChange: (String) -> Unit,
    navigateToMangaDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            label = { Text(text = stringResource(id = R.string.search_bar_hint)) },
            onValueChange = onValueChange,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null
                )
            }
        )

        Column {
            MangaList(
                manga = manga,
                navigateToMangaDetail = navigateToMangaDetail,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    MangaReaderTheme {
        MangaSearchBar(
            manga = PreviewDataFactory.MANGA_LIST,
            value = "",
            onValueChange = {},
            navigateToMangaDetail = {}
        )
    }
}