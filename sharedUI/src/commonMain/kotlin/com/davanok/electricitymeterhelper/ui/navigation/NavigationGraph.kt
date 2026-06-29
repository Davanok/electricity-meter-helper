package com.davanok.electricitymeterhelper.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.davanok.electricitymeterhelper.ui.screens.home.HomeScreen
import com.davanok.electricitymeterhelper.ui.screens.info.InfoScreen
import com.davanok.electricitymeterhelper.ui.screens.reading.ReadingScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

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
                HomeScreen(
                    navigateToInfo = { backStack.add(Route.Info(it)) },
                    navigateToReading = { backStack.add(Route.Reading(Uuid.random())) },
                    viewModel = koinViewModel()
                )
            }
            entry<Route.Info> { (entryId: Uuid) ->
                InfoScreen(
                    navigateBack = {
                        backStack.clear()
                        backStack.add(Route.Home)
                    },
                    navigateToReading = { backStack.add(Route.Reading(entryId)) },
                    viewModel = koinViewModel { parametersOf(entryId) }
                )
            }
            entry<Route.Reading> { (entryId: Uuid?) ->
                ReadingScreen(
                    navigateBack = { backStack.removeLastOrNull() },
                    navigateToInfo = { backStack[backStack.lastIndex] = Route.Info(it) },
                    viewModel = koinViewModel { parametersOf(entryId) }
                )
            }
        }
    )
}