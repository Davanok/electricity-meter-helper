package com.davanok.electricitymeterhelper.ui.screens.reading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.electricitymeterhelper.domain.ReadingEntry
import com.davanok.electricitymeterhelper.domain.ReadingObject
import com.davanok.electricitymeterhelper.domain.ReadingObjectsRepository
import com.davanok.electricitymeterhelper.seed.Voroshilova27
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid

class ReadingViewModel(
    private val entryId: Uuid?,
    private val repository: ReadingObjectsRepository
) : ViewModel() {
    private val readingObjectId = entryId ?: Uuid.random()
    private val _uiState = MutableStateFlow(ReadingScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
        autoSaveTimer()
    }

    private fun loadData() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        if (entryId == null) {
            val entries = generateReadingEntries()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    entries = entries
                )
            }
            return@launch
        }

        repository.getObject(entryId).fold(
            onSuccess = { obj ->
                val entries = obj?.entries ?: generateReadingEntries()

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

    private suspend fun generateReadingEntries(): List<ReadingEntry> {
        val defaultValue: List<ReadingEntry> = Voroshilova27.map {
            ReadingEntry(
                apartment = it,
                previousValue = 0,
                currentValue = 0
            )
        }

        val previousObjects = repository
            .getObjectsMin()
            .getOrNull()
            .takeUnless { it.isNullOrEmpty() }
            ?: return defaultValue

        val latestObjectMin = previousObjects.maxBy { it.date }
        val latestObject = repository
            .getObject(latestObjectMin.id)
            .getOrNull()
            ?: return defaultValue
        val latestValues = latestObject.entries.associate { it.apartment to it.currentValue }

        return Voroshilova27.map { apartment ->
            ReadingEntry(
                apartment = apartment,
                previousValue = latestValues[apartment] ?: 0,
                currentValue = 0
            )
        }
    }
    fun setReadingValue(index: Int, value: Int) {
        _uiState.update {
            val entries = it.entries.toMutableList()
            entries[index] = entries[index].copy(currentValue = value)
            it.copy(entries = entries)
        }
    }
    fun moveToItem(index: Int) {
        _uiState.update {
            it.copy(currentReadingEntryIndex = index)
        }
    }

    private fun autoSaveTimer() = viewModelScope.launch {
        while (viewModelScope.isActive) {
            saveData()
            delay(1.seconds)
        }
    }

    fun saveData(onSuccess: (Uuid) -> Unit = {}) = viewModelScope.launch {
        val obj = ReadingObject(
            id = readingObjectId,
            date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            entries = uiState.value.entries
        )
        repository.setObject(obj).fold(
            onSuccess = { onSuccess(readingObjectId) },
            onFailure = { thr -> _uiState.update { it.copy(errorMessage = thr.message) } }
        )
    }
}