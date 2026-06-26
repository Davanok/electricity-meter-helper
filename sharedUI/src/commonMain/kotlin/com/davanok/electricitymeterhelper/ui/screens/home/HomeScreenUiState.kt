package com.davanok.electricitymeterhelper.ui.screens.home

import androidx.compose.runtime.Immutable
import com.davanok.electricitymeterhelper.domain.ReadingObjectMin

@Immutable
data class HomeScreenUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val entries: List<ReadingObjectMin> = emptyList()
)
