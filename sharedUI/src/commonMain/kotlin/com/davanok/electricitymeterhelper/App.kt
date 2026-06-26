package com.davanok.electricitymeterhelper

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.davanok.electricitymeterhelper.theme.AppTheme
import com.davanok.electricitymeterhelper.ui.navigation.NavigationGraph

@Preview
@Composable
fun App(
    onThemeChanged: @Composable (isDark: Boolean) -> Unit = {}
) = AppTheme(onThemeChanged) {
    Scaffold { paddingValues ->
        NavigationGraph(
            modifier = Modifier.padding(paddingValues)
        )
    }
}
