package com.davanok.electricitymeterhelper.ui.screens.reading

import androidx.lifecycle.ViewModel
import com.davanok.electricitymeterhelper.domain.ReadingObjectsRepository
import kotlin.uuid.Uuid

class ReadingViewModel(
    private val entryId: Uuid,
    private val repository: ReadingObjectsRepository
) : ViewModel() {
}