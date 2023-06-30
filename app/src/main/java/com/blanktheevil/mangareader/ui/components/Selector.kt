package com.blanktheevil.mangareader.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import java.util.Locale

@Composable
fun Selector(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf( selectedItem) }
    val locale = LocalContext.current.resources.configuration.locales[0]

    fun String.cap(locale: Locale): String {
        return this.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                locale
            ) else it.toString()
        }
    }

    Column {
        OutlinedButton(
            shape = RoundedCornerShape(4.dp),
            modifier = modifier,
            onClick = { expanded = true },
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 8.dp,
                top = 8.dp,
                bottom = 8.dp
            )
        ) {
            if (items.isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = selected.cap(locale)
                )
            }
            Icon(
                imageVector = Icons.Rounded.ArrowDropDown,
                contentDescription = null
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach {
                DropdownMenuItem(
                    text = { Text(text = it.cap(locale)) },
                    onClick = {
                        expanded = false
                        selected = it
                        onItemSelected(it)
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MangaReaderTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp).padding(bottom = 100.dp)) {
                Selector(items = listOf("item1", "item2", "item3"), selectedItem = "item1", onItemSelected = {})
            }
        }
    }
}