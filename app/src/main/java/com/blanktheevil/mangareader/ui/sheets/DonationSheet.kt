package com.blanktheevil.mangareader.ui.sheets

import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme

@Composable
fun DonationSheetLayout() {
    val coffeeImage = painterResource(id = R.drawable.cute_coffee)
    val tipLinkIcon = painterResource(id = R.drawable.round_open_in_new_24)

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        onResult = {}
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(vertical = 32.dp)
            .padding(bottom = 96.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Text(text = "Hello!", style = MaterialTheme.typography.displayLarge)

            Image(
                painter = coffeeImage,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                modifier = Modifier.padding(horizontal = 56.dp),
                text = "If you really enjoy this app, consider sending me a tip!",
                textAlign = TextAlign.Center
            )

            Button(onClick = {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://www.buymeacoffee.com/blanktheevil")
                launcher.launch(intent)
            }) {
                Text(text = "Send a Tip!", modifier = Modifier.padding(end = 8.dp))
            }
        }
    }
}

@Preview()
@Composable
private fun PreviewLight() {
    MangaReaderTheme {
        Surface {
            DonationSheetLayout()
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewDark() {
    MangaReaderTheme {
        Surface {
            DonationSheetLayout()
        }
    }
}