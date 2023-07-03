package com.blanktheevil.mangareader.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.domain.ComposableError
import com.blanktheevil.mangareader.domain.LoginUsernameError

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocused: () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: String? = null,
    supportingIcon: ImageVector? = null,
    labelText: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) = OutlinedTextField(
    modifier = modifier
        .onFocusChanged {
            if (it.isFocused) {
                onFocused()
            }
        },
    value = value,
    onValueChange = onValueChange,
    isError = isError,
    supportingText =
        supportingText?.let { text ->
            { Row {
                supportingIcon?.let { icon ->
                    Icon(imageVector = icon, contentDescription = null)
                }
                Text(text = text)
            }}
        }
    ,
    label = labelText?.let { { Text(text = labelText) } },
    visualTransformation = visualTransformation
)

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocused: () -> Unit,
    modifier: Modifier = Modifier,
    error: ComposableError? = null,
    labelText: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) = OutlinedTextField(
    modifier = modifier
        .onFocusChanged {
            if (it.isFocused) {
                onFocused()
            }
        },
    value = value,
    onValueChange = onValueChange,
    isError = error != null,
    supportingText =
        error?.let { e ->
            { Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.height(16.dp),
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null
                )
                Text(text = e.getErrorString())
            }}
        }
    ,
    label = labelText?.let { { Text(text = labelText) } },
    visualTransformation = visualTransformation,
    keyboardOptions = keyboardOptions,
    keyboardActions = keyboardActions,
)

@Preview(showBackground = true)
@Composable
private fun Preview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InputField(value = "hi", onValueChange = {}, onFocused = {}, keyboardOptions = KeyboardOptions.Default)
        InputField(value = "hi", onValueChange = {}, onFocused = {}, supportingText = "Hello World")
        InputField(value = "hi", onValueChange = {}, onFocused = {}, isError = true)
        InputField(value = "hi", onValueChange = {}, onFocused = {}, error = LoginUsernameError.INVALID)
    }
}