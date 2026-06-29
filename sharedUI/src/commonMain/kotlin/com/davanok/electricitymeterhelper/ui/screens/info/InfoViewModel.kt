package com.davanok.electricitymeterhelper.ui.screens.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.electricitymeterhelper.domain.ReadingObjectsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

class InfoViewModel(
    private val entryId: Uuid,
    private val repository: ReadingObjectsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(InfoScreenUiState())
    val uiState = _uiState.asStateFlow()

    fun loadData() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        repository.getObject(entryId).fold(
            onSuccess = { data ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                        data = data
                    )
                }
            },
            onFailure = { thr ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = thr.message
                    )
                }
            }
        )
    }
}