package com.davanok.electricitymeterhelper.ui.screens.home

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.Uuid

@Composable
fun HomeScreen(
    navigateToInfo: (entryId: Uuid) -> Unit,
    navigateToReading: () -> Unit,
    viewModel: HomeViewModel
) {

}