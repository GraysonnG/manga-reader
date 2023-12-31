package com.blanktheevil.mangareader.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.helpers.shortTitle
import com.blanktheevil.mangareader.helpers.title

@Composable
fun ChapterButton(
    mangaId: String,
    chapter: ChapterDto,
    isRead: Boolean,
    useShortTitle: Boolean = false,
    navigateToReader: (String, String) -> Unit,
) {
    if (chapter.attributes.externalUrl == null) {
        Button(
            shape = RoundedCornerShape(4.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp
            ),
            onClick = {
                navigateToReader(chapter.id, mangaId)
            },
            colors = if (isRead) ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            ) else ButtonDefaults.buttonColors()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(weight = 1f, fill = true),
                    text = (if (useShortTitle) chapter.shortTitle else chapter.title)
                        .ifEmpty { "No Data" },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = Icons.Rounded.ArrowForward,
                    contentDescription = null
                )
            }
        }
    } else {
        OpenWebsiteButton(
            url = chapter.attributes.externalUrl,
            text = if (useShortTitle) chapter.shortTitle else chapter.title,
            isRead = isRead
        )
    }
}

@Preview
@Composable
private fun Preview2() {
    DefaultPreview {
        Column {
            ChapterButton(
                mangaId = StubData.MANGA.id,
                chapter = StubData.CHAPTER,
                isRead = false,
            ) { _, _ -> }
            ChapterButton(
                mangaId = StubData.MANGA.id,
                chapter = StubData.CHAPTER,
                isRead = true,
            ) { _, _ -> }
        }
    }
}