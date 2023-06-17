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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.PreviewDataFactory
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.helpers.title
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme

@Composable
fun ChapterButton(
    mangaId: String,
    chapter: ChapterDto,
    isRead: Boolean,
    navigateToReader: (String, String) -> Unit,
) {
    Button(
        shape = RoundedCornerShape(4.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp
        ),
        onClick = {
            navigateToReader(chapter.id, mangaId)
        },
        colors = if(isRead) ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ) else ButtonDefaults.buttonColors()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(weight = 1f, fill = true),
                text = chapter.title.ifEmpty { "No Data" },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = Icons.Rounded.ArrowForward,
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
private fun Preview2() {
    MangaReaderTheme {
        Column {
            ChapterButton(
                mangaId = PreviewDataFactory.MANGA.id,
                chapter = PreviewDataFactory.CHAPTER,
                isRead = false,
            ) {_,_->}
            ChapterButton(
                mangaId = PreviewDataFactory.MANGA.id,
                chapter = PreviewDataFactory.CHAPTER,
                isRead = true,
            ) {_,_->}
        }
    }
}