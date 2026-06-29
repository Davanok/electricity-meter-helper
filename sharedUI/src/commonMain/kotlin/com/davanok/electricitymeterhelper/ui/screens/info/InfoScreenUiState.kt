package com.davanok.electricitymeterhelper.ui.screens.info

import androidx.compose.runtime.Immutable
import com.davanok.electricitymeterhelper.domain.ReadingObject

@Immutable
data class InfoScreenUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val data: ReadingObject? = null
)
