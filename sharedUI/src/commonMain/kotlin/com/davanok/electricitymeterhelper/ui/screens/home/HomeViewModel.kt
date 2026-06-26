package com.davanok.electricitymeterhelper.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.electricitymeterhelper.domain.ReadingObjectsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ReadingObjectsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadReadingObjects()
    }

    fun loadReadingObjects() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        repository.getObjectsMin().fold(
            onSuccess = { entries ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        entries = entries
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