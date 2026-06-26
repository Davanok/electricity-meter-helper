package com.davanok.electricitymeterhelper.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.expressiveLightColorScheme
import androidx.compose.runtime.*


internal val LocalThemeIsDark = compositionLocalOf { mutableStateOf(true) }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun AppTheme(
    onThemeChanged: @Composable (isDark: Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    val systemIsDark = isSystemInDarkTheme()
    val isDarkState = remember(systemIsDark) { mutableStateOf(systemIsDark) }
    CompositionLocalProvider(
        LocalThemeIsDark provides isDarkState
    ) {
        val isDark by isDarkState
        onThemeChanged(!isDark)
        MaterialTheme(
            colorScheme = if (isDark) darkColorScheme() else expressiveLightColorScheme(),
            content = { Surface(content = content) }
        )
    }
}
