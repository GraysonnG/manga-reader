package com.blanktheevil.mangareader.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.helpers.title

@Composable
fun ChapterButton(
    mangaId: String,
    chapter: ChapterDto,
    navigateToReader: (String, String) -> Unit,
) {
    Button(
        shape = RoundedCornerShape(4.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp
        ),
        onClick = {
            navigateToReader(chapter.id, mangaId)
        }
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