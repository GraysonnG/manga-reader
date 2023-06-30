package com.blanktheevil.mangareader.ui.components

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.PreviewDataFactory
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme

@Composable
fun OpenWebsiteButton(
    url: String,
    text: String,
    isRead: Boolean,
    handleResult: (ActivityResult) -> Unit = {},
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        onResult = handleResult
    )

    val icon = painterResource(id = R.drawable.open_in_new)

    Column {
        Button(
            shape = RoundedCornerShape(4.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp
            ),
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                launcher.launch(intent)
            },
            colors = if(isRead) ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            ) else ButtonDefaults.buttonColors()
        ) {
            Text(
                modifier = Modifier.weight(weight = 1f, fill = true),
                text = text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                modifier = Modifier.height(16.dp),
                painter = icon,
                contentDescription = null
            )
        }
    }
}

@Composable
@Preview
private fun OpenWebsiteButtonPreview() {
    MangaReaderTheme {
        Column {
            OpenWebsiteButton(
                url = "https://www.google.com",
                text = PreviewDataFactory.LONG_TEXT,
                isRead = false
            )
            
            OpenWebsiteButton(
                url = "https://www.google.com",
                text = PreviewDataFactory.LONG_TEXT,
                isRead = true
            )
        }
    }
}