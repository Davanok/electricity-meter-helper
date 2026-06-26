package com.davanok.electricitymeterhelper

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.davanok.electricitymeterhelper.di.appModule
import com.davanok.electricitymeterhelper.di.platformModule
import com.davanok.electricitymeterhelper.di.viewModelsModule
import com.davanok.electricitymeterhelper.theme.AppTheme
import com.davanok.electricitymeterhelper.ui.navigation.NavigationGraph
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration


@Composable
fun App(
    onThemeChanged: (isDark: Boolean) -> Unit = {},
) {
    KoinApplication(
        configuration = koinConfiguration {
            modules(
                appModule(),
                viewModelsModule(),
                platformModule()
            )
        }
    ) {

    }
    AppTheme(onThemeChanged) {
        Scaffold { paddingValues ->
            NavigationGraph(
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}
