package com.davanok.electricitymeterhelper.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay

@Composable
fun NavigationGraph(
    modifier: Modifier = Modifier
) {
    val backStack = mutableStateListOf<Route>(Route.Home)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Route.Home> {

            }
            entry<Route.Data> {

            }
            entry<Route.Reading> {

            }
        }
    )
}