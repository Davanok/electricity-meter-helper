package com.davanok.electricitymeterhelper.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.expressiveLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember


internal val LocalThemeIsDark = compositionLocalOf { mutableStateOf(true) }
internal val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("Snackbar host state not provided")
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun AppTheme(
    onThemeChanged: (isDark: Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    val systemIsDark = isSystemInDarkTheme()
    val isDarkState = remember(systemIsDark) { mutableStateOf(systemIsDark) }
    val snackbarHostState = remember { SnackbarHostState() }
    CompositionLocalProvider(
        LocalThemeIsDark provides isDarkState,
        LocalSnackbarHostState provides snackbarHostState
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) {
            val isDark by isDarkState
            LaunchedEffect(isDark) { onThemeChanged(!isDark) }

            MaterialTheme(
                colorScheme = if (isDark) darkColorScheme() else expressiveLightColorScheme(),
                content = { Surface(content = content) }
            )
        }
    }
}
