package com.blanktheevil.mangareader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.dto.utils.MangaList
import com.blanktheevil.mangareader.data.dto.utils.manga.toMangaList
import com.blanktheevil.mangareader.ui.RoundedCornerXSmall
import com.blanktheevil.mangareader.ui.SpacerSmall
import com.blanktheevil.mangareader.ui.smallDp
import com.blanktheevil.mangareader.ui.theme.Typography
import com.valentinilk.shimmer.shimmer

@Composable
fun MangaShelf(
    title: String,
    list: MangaList,
    loading: Boolean,
    modifier: Modifier = Modifier,
    onTitleClicked: (() -> Unit)? = null,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = onTitleClicked != null,
                    role = Role.Button
                ) {
                    onTitleClicked?.invoke()
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (loading) {
                Box(
                    Modifier
                        .clip(RoundedCornerXSmall)
                        .width(256.dp)
                        .height(IntrinsicSize.Min)
                        .shimmer(),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary.copy(0.25f))
                    )
                    Text(
                        text = "",
                        style = Typography.headlineSmall,
                    )
                }
            } else {
                Text(
                    text = title,
                    style = Typography.headlineSmall,
                    maxLines = 1,
                )

                if (onTitleClicked != null) {
                    Icon(imageVector = Icons.Rounded.ArrowForward, contentDescription = null)
                }
            }
        }
        SpacerSmall()
        HorizontalDivider(
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.primary
        )
        SpacerSmall()
        LazyRow(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(smallDp)
        ) {
            val cardModifier = Modifier.width(256.dp.div(3).times(2))


            if (
                loading
            ) {
                items(4) {
                    MangaCardShimmer(modifier = cardModifier)
                }
            } else {
                items(list, key = { it.id }) {
                    MangaCard(
                        modifier = cardModifier,
                        manga = it
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    DefaultPreview {
        Surface {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MangaShelf(
                    title = "The Title",
                    StubData.Data.MANGA_LIST.toMangaList(),
                    loading = false,
                    onTitleClicked = {},
                )
                MangaShelf(
                    title = "The Title",
                    emptyList(),
                    loading = true,
                    onTitleClicked = {},
                )
            }
        }
    }
}