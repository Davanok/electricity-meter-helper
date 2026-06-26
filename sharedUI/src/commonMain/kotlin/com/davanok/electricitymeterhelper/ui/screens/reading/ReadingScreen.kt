package com.davanok.electricitymeterhelper.ui.screens.reading

import androidx.compose.runtime.Composable
import kotlin.uuid.Uuid

@Composable
fun ReadingScreen(
    navigateToHome: () -> Unit,
    navigateToInfo: (entryId: Uuid) -> Unit,
    viewModel: ReadingViewModel
) {

}