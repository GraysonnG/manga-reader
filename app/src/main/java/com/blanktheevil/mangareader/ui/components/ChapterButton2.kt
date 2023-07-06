package com.blanktheevil.mangareader.ui.components

import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.PreviewDataFactory
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.getScanlationGroupRelationship
import com.blanktheevil.mangareader.helpers.shortTitle
import com.blanktheevil.mangareader.helpers.title
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme

@Composable
fun ChapterButton2(
    chapter: ChapterDto,
    isRead: Boolean,
    isDownloaded: Boolean = false,
    showDownload: Boolean = false,
    isDownloading: Boolean = false,
    useShortTitle: Boolean = false,
    navigateToReader: (String) -> Unit,
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        onResult = {}
    )
    val scanlationGroup = chapter.getScanlationGroupRelationship()

    val buttonColors = if(isRead) ButtonDefaults.buttonColors(
        containerColor = Color.Gray,
        contentColor = Color.White,
    ) else ButtonDefaults.buttonColors()

    val onButtonClicked = if (chapter.attributes.externalUrl == null) {
        { navigateToReader(chapter.id) }
    } else { {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(chapter.attributes.externalUrl)
        launcher.launch(intent)
    } }

    val trailingIcon: @Composable () -> Unit = if (chapter.attributes.externalUrl == null) {
        { Icon(
            painter = painterResource(id = R.drawable.round_chevron_right_24),
            contentDescription = null
        ) }
    } else {
        { Icon(
            modifier = Modifier.height(16.dp),
            painter = painterResource(id = R.drawable.round_open_in_new_24),
            contentDescription = null
        ) }
    }

    val downloadIcon = if (isDownloaded) {
        painterResource(id = R.drawable.round_check_24)
    } else {
        painterResource(id = R.drawable.twotone_download_24)
    }

    val downloadIconColors =
        IconButtonDefaults.iconButtonColors(
            contentColor =
                if (!isDownloaded) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.primary
                }
        )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        )
    ) {
        Column(
            modifier = Modifier
                .padding(
                    horizontal = 8.dp,
                    vertical = 4.dp
                )
                .offset(y = (-4).dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    modifier = Modifier.weight(weight = 1f, fill = true),
                    shape = RoundedCornerShape(4.dp),
                    colors = buttonColors,
                    onClick = onButtonClicked,
                    contentPadding = PaddingValues(
                        start = 24.dp,
                        end = 6.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    ),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = Modifier.weight(weight = 1f, fill = true),
                            text = if (useShortTitle) chapter.shortTitle else chapter.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        trailingIcon()
                    }
                }

                if (showDownload) {
                    if (isDownloading) {
                        CircularProgressIndicator(modifier = Modifier
                            .padding(4.dp)
                            .size(28.dp),
                            color = MaterialTheme.colorScheme.primary.copy(0.5f)
                        )
                    } else {
                        IconButton(
                            colors = downloadIconColors,
                            modifier = Modifier.size(36.dp),
                            onClick = { /*TODO*/ }) {
                            Icon(downloadIcon, contentDescription = null)
                        }
                    }
                }
            }

            scanlationGroup?.let {
                GroupButton(group = it)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    MangaReaderTheme {
        Surface {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ChapterButton2(
                    chapter = PreviewDataFactory.CHAPTER,
                    isRead = false,
                    navigateToReader = {}
                )

                ChapterButton2(
                    chapter = PreviewDataFactory.CHAPTER,
                    isRead = true,
                    navigateToReader = {}
                )

                ChapterButton2(
                    chapter = PreviewDataFactory.CHAPTER,
                    isRead = false,
                    showDownload = true,
                    isDownloaded = false,
                    navigateToReader = {}
                )

                ChapterButton2(
                    chapter = PreviewDataFactory.CHAPTER,
                    isRead = false,
                    showDownload = true,
                    isDownloaded = true,
                    navigateToReader = {}
                )

                ChapterButton2(
                    chapter = PreviewDataFactory.CHAPTER,
                    isRead = false,
                    showDownload = true,
                    isDownloading = true,
                    navigateToReader = {}
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewDark() {
    MangaReaderTheme {
        Surface {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ChapterButton2(
                    chapter = PreviewDataFactory.CHAPTER,
                    isRead = false,
                    navigateToReader = {}
                )

                ChapterButton2(
                    chapter = PreviewDataFactory.CHAPTER,
                    isRead = true,
                    navigateToReader = {}
                )

                ChapterButton2(
                    chapter = PreviewDataFactory.CHAPTER,
                    isRead = false,
                    isDownloaded = true,
                    navigateToReader = {}
                )

                ChapterButton2(
                    chapter = PreviewDataFactory.CHAPTER,
                    isRead = true,
                    showDownload = false,
                    navigateToReader = {}
                )
            }
        }
    }
}