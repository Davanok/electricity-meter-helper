package com.davanok.electricitymeterhelper.ui.screens.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.softwork.serialization.csv.CSVFormat
import com.davanok.electricitymeterhelper.domain.ReadingObjectsRepository
import com.davanok.electricitymeterhelper.domain.SaveReadingObjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid
import com.davanok.electricitymeterhelper.platform.saveFile
import com.davanok.electricitymeterhelper.utils.DateFormat
import electricitymeterhelper.sharedui.generated.resources.Res
import electricitymeterhelper.sharedui.generated.resources.output_filename
import kotlinx.datetime.format
import org.jetbrains.compose.resources.getString

class InfoViewModel(
    private val entryId: Uuid,
    private val repository: ReadingObjectsRepository,
    private val savingRepository: SaveReadingObjectRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(InfoScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

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

    fun saveFile() {
        val obj = uiState.value.data ?: return
        viewModelScope.launch {
            val dateString = obj.date.format(DateFormat)
            val filename = getString(Res.string.output_filename, dateString)
            savingRepository.saveReadingObject(filename, obj)
        }
    }
}