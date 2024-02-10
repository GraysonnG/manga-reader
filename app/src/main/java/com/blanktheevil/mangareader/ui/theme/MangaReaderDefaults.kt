package com.blanktheevil.mangareader.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.blanktheevil.mangareader.UIError

object MangaReaderDefaults {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun topAppBarColors() = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primary,
        scrolledContainerColor = MaterialTheme.colorScheme.primary,
        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
        titleContentColor = MaterialTheme.colorScheme.onPrimary,
        actionIconContentColor = Color.Unspecified,
    )

    @Composable
    fun BackArrowIconButton(
        onClick: () -> Unit
    ) = IconButton(onClick = onClick) {
        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
    }

    @Composable
    fun DefaultErrorSnackBar(
        snackbarHostState: SnackbarHostState,
        error: UIError,
    ) {
        Snackbar(
            dismissAction = {
                TextButton(
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    onClick = {
                        snackbarHostState.currentSnackbarData?.dismiss()
                    }
                ) {
                    Text(text = "Ok")
                }
            },
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            dismissActionContentColor = MaterialTheme.colorScheme.onErrorContainer,
        ) {
            Column {
                Text(text = "Error: ${error.getErrorTitle()}")
            }
        }
    }

    @Composable
    fun DefaultErrorSnackBar(
        snackbarHostState: SnackbarHostState,
    ) {
        Snackbar(
            dismissAction = {
                TextButton(
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    onClick = {
                        snackbarHostState.currentSnackbarData?.dismiss()
                    }
                ) {
                    Text(text = "Ok")
                }
            },
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            dismissActionContentColor = MaterialTheme.colorScheme.onErrorContainer,
        ) {
            Column {
                Text(text = "Error: ${snackbarHostState.currentSnackbarData?.visuals?.message}")
            }
        }
    }
}