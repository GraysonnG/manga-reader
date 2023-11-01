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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.LocalNavController
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.Chapter
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.toChapter
import com.blanktheevil.mangareader.navigation.navigateToReader
import com.blanktheevil.mangareader.ui.mediumDp
import org.koin.compose.koinInject

@Composable
fun ChapterButton2(
    modifier: Modifier = Modifier,
    chapter: Chapter,
    followingIcon: @Composable () -> Unit = {},
    useShortTitle: Boolean = false,
) {
    val navController = LocalNavController.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        onResult = {}
    )

    val buttonColors = if (chapter.isRead) ButtonDefaults.buttonColors(
        containerColor = Color.Gray,
        contentColor = Color.White,
    ) else ButtonDefaults.buttonColors()

    val onButtonClicked = if (chapter.externalUrl == null) {
        { navController.navigateToReader(chapter.id) }
    } else {
        {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(chapter.externalUrl)
            launcher.launch(intent)
        }
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
                            text = if (useShortTitle) chapter.shortTitle else chapter.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        trailingIcon()
                    }
                }

                followingIcon()
            }

            chapter.relatedScanlationGroup?.let {
                GroupButton(group = it)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    DefaultPreview {
        Surface {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ChapterButton2(
                    chapter = StubData.CHAPTER.toChapter(moshi = koinInject()),
                )

                ChapterButton2(
                    chapter = StubData.CHAPTER.toChapter(moshi = koinInject()),
                )

                ChapterButton2(
                    chapter = StubData.CHAPTER.toChapter(moshi = koinInject()),
                    followingIcon = {
                        Icon(Icons.Rounded.Check, contentDescription = null)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewDark() {
    DefaultPreview {
        Surface {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ChapterButton2(
                    chapter = StubData.CHAPTER.toChapter(moshi = koinInject()),
                )

                ChapterButton2(
                    chapter = StubData.CHAPTER.toChapter(moshi = koinInject()),
                )

                ChapterButton2(
                    chapter = StubData.CHAPTER.toChapter(moshi = koinInject()),
                    followingIcon = {
                        Icon(Icons.Rounded.Check, contentDescription = null)
                    }
                )
            }
        }
    }
}