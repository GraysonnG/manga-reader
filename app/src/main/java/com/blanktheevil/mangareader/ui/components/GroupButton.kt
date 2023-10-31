package com.blanktheevil.mangareader.ui.components

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.dto.ChapterScanlationGroupDto

data class GroupButtonColors(
    val contentColor: Color,
    val containerColor: Color
)

@Composable
fun groupButtonColors(
    contentColor: Color = LocalContentColor.current,
    containerColor: Color = Color.Transparent,
): GroupButtonColors = GroupButtonColors(
    contentColor = contentColor,
    containerColor = containerColor,
)

@Composable
fun GroupButton(
    group: ChapterScanlationGroupDto,
    colors: GroupButtonColors = groupButtonColors(),
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        onResult = {}
    )
    val groupIcon = painterResource(id = R.drawable.round_group_24)

    Row(
        Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(group.attributes?.website ?: return@clickable)
                launcher.launch(intent)
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.height(16.dp),
            tint = colors.contentColor,
            painter = groupIcon,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            color = colors.contentColor,
            style = MaterialTheme.typography.bodySmall,
            text = group.attributes?.name ?: "No Scanlation Group"
        )
    }
}