package com.davanok.electricitymeterhelper.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.davanok.electricitymeterhelper.theme.LocalSnackbarHostState

@Composable
fun SnackbarMessageHandler(message: String?) {
    val snackbarHostState = LocalSnackbarHostState.current
    LaunchedEffect(message) {
        if (message != null)
            snackbarHostState.showSnackbar(message)
    }
}