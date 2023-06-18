package com.blanktheevil.mangareader.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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
    val hint = stringResource(id = R.string.search_bar_hint)

    Column(
        modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            label = {
                Text(text = hint)
            },
            onValueChange = onValueChange,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null
                )
            }
        )

        val color = if (isSystemInDarkTheme()) {
            Color.White
        } else {
            Color.Black
        }



        if (manga.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(0.1f))
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                MangaList(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .zIndex(2f),
                    manga = manga,
                    navigateToMangaDetail = navigateToMangaDetail,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    MangaReaderTheme {
        Surface(
            Modifier.fillMaxSize()
        ) {
            Column {
                MangaSearchBar(
                    manga = PreviewDataFactory.MANGA_LIST,
                    value = "",
                    onValueChange = {},
                    navigateToMangaDetail = {}
                )

                Text(text = PreviewDataFactory.LONG_TEXT)
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
    MangaReaderTheme() {
        Surface(
            Modifier.fillMaxSize()
        ) {
            Column {
                MangaSearchBar(
                    manga = PreviewDataFactory.MANGA_LIST,
                    value = "",
                    onValueChange = {},
                    navigateToMangaDetail = {}
                )

                Text(text = PreviewDataFactory.LONG_TEXT)
            }
        }
    }
}