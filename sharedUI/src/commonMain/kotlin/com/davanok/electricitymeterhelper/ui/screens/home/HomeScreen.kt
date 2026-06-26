package com.davanok.electricitymeterhelper.ui.screens.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.uuid.Uuid

@Composable
fun HomeScreen(
    navigateToInfo: (entryId: Uuid) -> Unit,
    navigateToReading: () -> Unit,
    viewModel: HomeViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Content(
        uiState = uiState,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun Content(
    uiState: HomeScreenUiState,
    modifier: Modifier = Modifier
) {

}