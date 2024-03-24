package com.blanktheevil.mangareader.ui.components

import android.content.Intent
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.Chapter
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.dto.utils.chapter.toChapter
import com.blanktheevil.mangareader.data.reader.ReaderManager
import com.blanktheevil.mangareader.ui.mediumDp
import org.koin.compose.koinInject

@Composable
fun ChapterButton(
    modifier: Modifier = Modifier,
    chapter: Chapter,
    followingIcon: @Composable () -> Unit = {},
    useShortTitle: Boolean = false,
    useMediumTitle: Boolean = false,
) {
    val readerManager = koinInject<ReaderManager>()
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        onResult = {}
    )

    val buttonColors = if (chapter.isRead) ButtonDefaults.buttonColors(
        containerColor = Color.Gray,
        contentColor = Color.White,
    ) else ButtonDefaults.buttonColors()

    val onButtonClicked by remember {
        mutableStateOf(if (chapter.externalUrl == null) {
            { readerManager.setChapter(chapter.id) }
        } else {
            {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(chapter.externalUrl)
                launcher.launch(intent)
            }
        })
    }

    val trailingIcon: @Composable () -> Unit = if (chapter.externalUrl == null) {
        {
            Icon(
                painter = painterResource(id = R.drawable.round_chevron_right_24),
                contentDescription = null
            )
        }
    } else {
        {
            Icon(
                modifier = Modifier.height(mediumDp),
                painter = painterResource(id = R.drawable.round_open_in_new_24),
                contentDescription = null
            )
        }
    }

    Card(
        modifier = modifier.widthIn(0.dp, 600.dp),
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
                .offset(y = 4.dp.unaryMinus())
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                            text = when {
                                useShortTitle -> chapter.shortTitle
                                useMediumTitle -> chapter.mediumTitle
                                else -> chapter.title
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        trailingIcon()
                    }
                }

                followingIcon()
            }

            chapter.relatedScanlationGroup?.let {
                Row(
                    modifier = Modifier
                        .zIndex(-1f)
                        .offset(y = 8.dp.unaryMinus(), x = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(id = R.drawable.round_subdirectory_arrow_right_24),
                        contentDescription = null,
                    )

                    GroupButton(
                        modifier = Modifier,
                        group = it
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
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ChapterButton(
                    chapter = StubData.Data.CHAPTER.toChapter(),
                )

                ChapterButton(
                    chapter = StubData.Data.CHAPTER.toChapter(),
                )

                ChapterButton(
                    chapter = StubData.Data.CHAPTER.toChapter(),
                    followingIcon = {
                        Icon(Icons.Rounded.Check, contentDescription = null)
                    }
                )
            }
        }
    }
}