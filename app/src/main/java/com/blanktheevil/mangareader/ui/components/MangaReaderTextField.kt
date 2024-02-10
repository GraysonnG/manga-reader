package com.blanktheevil.mangareader.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.blanktheevil.mangareader.ui.RoundedCornerXLarge
import com.blanktheevil.mangareader.ui.SpacerXSmall

@Composable
fun MangaReaderTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean = false,
    singleLine: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        leadingIcon = leadingIcon,
        trailingIcon = {
            trailingIcon?.let {
                Row {
                    it()
                    SpacerXSmall()
                }
            }
        },
        placeholder = placeholder,
        maxLines = 1,
        readOnly = readOnly,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        shape = RoundedCornerXLarge,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            disabledBorderColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceDim,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceDim,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceDim,
        ),
        textStyle = MaterialTheme.typography.labelMedium
    )
}