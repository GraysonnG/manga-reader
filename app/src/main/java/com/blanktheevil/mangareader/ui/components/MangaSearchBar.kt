package com.blanktheevil.mangareader.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.ui.SpacerXSmall

@Composable
fun MangaSearchBar(
    manga: List<MangaDto>,
    value: String,
    onValueChange: (String) -> Unit,
    navigateToMangaDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors()
) {
    val hint = stringResource(id = R.string.search_bar_hint)

    Column(
        modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = value,
            label = {
                Text(text = hint)
            },
            onValueChange = onValueChange,
            trailingIcon = {
                if (value.isEmpty()) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null
                    )
                } else {
                    IconButton(onClick = {
                        onValueChange("")
                    }) {
                        Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
                    }
                }
            },
            colors = colors,
        )

        if (manga.isNotEmpty()) {
            SpacerXSmall()
            MangaList(
                modifier = Modifier
                    .zIndex(2f),
                manga = manga,
                navigateToMangaDetail = navigateToMangaDetail,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    DefaultPreview {
        Surface(
            Modifier.fillMaxSize()
        ) {
            Column {
                MangaSearchBar(
                    manga = StubData.MANGA_LIST,
                    value = "",
                    onValueChange = {},
                    navigateToMangaDetail = {}
                )

                Text(text = StubData.LONG_TEXT)
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun Preview2() {
    DefaultPreview {
        Surface(
            Modifier.fillMaxSize()
        ) {
            Column {
                MangaSearchBar(
                    manga = StubData.MANGA_LIST,
                    value = "",
                    onValueChange = {},
                    navigateToMangaDetail = {}
                )

                Text(text = StubData.LONG_TEXT)
            }
        }
    }
}