package com.blanktheevil.mangareader.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.PopupProperties
import com.blanktheevil.mangareader.DebouncedValue
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.success
import com.blanktheevil.mangareader.ui.smallDp
import kotlinx.coroutines.withContext

/**
 * given a text input use the provided viewmodel method to get data from the api
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalComposeUiApi::class)
@Composable
fun <T> SearchSelector(
    modifier: Modifier = Modifier,
    getData: suspend (String) -> Result<List<T>>,
    onValueChange: (List<T>) -> Unit,
    itemTitle: @Composable (T) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val debouncer by remember {
        mutableStateOf(DebouncedValue("", 1000, coroutineScope))
    }
    val inputText by debouncer.asStateFlow().collectAsState()
    var text by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var hasFocus by remember { mutableStateOf(false) }
    var dataList: List<T> by remember { mutableStateOf(emptyList()) }
    var selectedList: List<T> by remember { mutableStateOf(emptyList()) }
    val textValue = if (!hasFocus) {
        selectedList.joinToString(", ") { it.toString() }
    } else {
        text
    }

    fun onInputChanged(newText: String) {
        debouncer.value = newText
        text = newText
    }

    LaunchedEffect(inputText) {
        withContext(coroutineScope.coroutineContext) {
            if (inputText.isNotEmpty()) {
                getData(inputText).onSuccess {
                    dataList = it
                    expanded = true
                }
            }
        }
    }

    Column(
        modifier = modifier.onFocusChanged {
            expanded = it.hasFocus
            hasFocus = it.hasFocus
        }
    ) {
        OutlinedTextField(
            value = textValue,
            onValueChange = ::onInputChanged,
        )
        FlowRow(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(smallDp)
        ) {
            selectedList.forEach {
                SuggestionChip(
                    onClick = {
                        selectedList -= it
                        onValueChange(selectedList)
                    },
                    label = { itemTitle(it) }
                )
            }
        }
        DropdownMenu(
            expanded = expanded && dataList.isNotEmpty(),
            onDismissRequest = { expanded = false },
            properties = PopupProperties(
                focusable = false,
                usePlatformDefaultWidth = true
            )
        ) {
            dataList.forEach {
                DropdownMenuItem(
                    text = { itemTitle(it) },
                    onClick = {
                        if (it !in selectedList) {
                            selectedList += it
                            onValueChange(selectedList)
                        }
                    },
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewSearchSelector() {
    DefaultPreview {
        SearchSelector(
            getData = {
                success(StubData.TAGS.take(10))
            },
            onValueChange = {},
            itemTitle = {
                Text(text = it.name)
            }
        )
    }
}