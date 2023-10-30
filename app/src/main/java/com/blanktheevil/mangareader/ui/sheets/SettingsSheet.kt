package com.blanktheevil.mangareader.ui.sheets

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.settings.SettingsManager
import com.blanktheevil.mangareader.ui.components.LabeledCheckbox
import com.blanktheevil.mangareader.ui.components.Selector
import com.blanktheevil.mangareader.ui.theme.Theme
import org.koin.compose.koinInject

@Composable
fun SettingsSheetLayout(
    settingsManager: SettingsManager = koinInject()
) {
    var darkMode by remember { mutableStateOf(settingsManager.darkMode) }
    var theme by remember { mutableStateOf(settingsManager.theme) }
    var dataSaver by remember { mutableStateOf(settingsManager.dataSaver) }
    var filterSafe by remember {
        mutableStateOf(settingsManager.contentFilters.contains("safe"))
    }
    var filterSuggestive by remember {
        mutableStateOf(settingsManager.contentFilters.contains("suggestive"))
    }
    var filterEro by remember {
        mutableStateOf(settingsManager.contentFilters.contains("erotica"))
    }
    var filterNSFW by remember {
        mutableStateOf(settingsManager.contentFilters.contains("pornographic"))
    }

    LaunchedEffect(key1 = darkMode) {
        settingsManager.darkMode = darkMode
    }

    LaunchedEffect(key1 = theme) {
        settingsManager.theme = theme
    }

    LaunchedEffect(key1 = dataSaver) {
        settingsManager.dataSaver = dataSaver
    }

    LaunchedEffect(filterSafe, filterSuggestive, filterEro, filterNSFW) {
        settingsManager.contentFilters = mutableSetOf<String>().apply {
            if (filterSafe) add("safe")
            if (filterSuggestive) add("suggestive")
            if (filterEro) add("erotica")
            if (filterNSFW) add("pornographic")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            text = stringResource(id = R.string.settings_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Divider()

        Setting(
            title = stringResource(id = R.string.settings_dark_mode),
            subtitle = stringResource(id = R.string.settings_dark_mode_subtitle),
        ) {
            Selector(
                items = listOf("system", "dark", "light"),
                selectedItem = darkMode,
                onItemSelected = {
                    darkMode = it
                }
            )
        }

        Divider()

        Setting(
            title = stringResource(id = R.string.settings_theme),
            subtitle = stringResource(id = R.string.settings_theme_subtitle),
        ) {
            Selector(
                items = listOf(
                    Theme.PURPLE.savedName,
                    Theme.MANGA_DEX.savedName,
                    Theme.SYSTEM.savedName
                ),
                selectedItem = theme,
                onItemSelected = { theme = it },
            )
        }

        Divider()

        Setting(
            title = stringResource(id = R.string.settings_data_saver),
            subtitle = stringResource(id = R.string.settings_data_saver_subtitle),
        ) {
            Switch(checked = dataSaver, onCheckedChange = {
                dataSaver = it
            })
        }

        Divider()

        Setting(
            title = stringResource(id = R.string.settings_content_filter),
            subtitle = stringResource(id = R.string.settings_content_filter_subtitle),
        ) {
            Column {
                LabeledCheckbox(
                    text = stringResource(id = R.string.settings_content_filter_safe),
                    checked = filterSafe,
                    onCheckedChange = { filterSafe = it },
                    short = true
                )
                LabeledCheckbox(
                    text = stringResource(id = R.string.settings_content_filter_suggestive),
                    checked = filterSuggestive,
                    onCheckedChange = { filterSuggestive = it },
                    short = true
                )
                LabeledCheckbox(
                    text = stringResource(id = R.string.settings_content_filter_ero),
                    checked = filterEro,
                    onCheckedChange = { filterEro = it },
                    short = true
                )
                LabeledCheckbox(
                    text = stringResource(id = R.string.settings_content_filter_nsfw),
                    checked = filterNSFW,
                    onCheckedChange = { filterNSFW = it },
                    short = true
                )
            }
        }
    }
}

@Composable
private fun Setting(
    title: String,
    subtitle: String? = null,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.fillMaxWidth(0.5f)) {
            Text(text = title)
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            }
        }
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewScreenLight() {
    val context = LocalContext.current
    SettingsManager.getInstance().init(context)

    DefaultPreview {
        Surface {
            SettingsSheetLayout()
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewScreenDark() {
    val context = LocalContext.current
    SettingsManager.getInstance().init(context)

    DefaultPreview {
        Surface {
            SettingsSheetLayout()
        }
    }
}