package com.davanok.electricitymeterhelper.ui.screens.reading

import androidx.compose.runtime.Immutable
import com.davanok.electricitymeterhelper.domain.ReadingEntry

@Immutable
data class ReadingScreenUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val entries: List<ReadingEntry> = emptyList(),
    val currentReadingEntryIndex: Int = 0,
    val currentValue: Int = 0
)