package com.davanok.electricitymeterhelper.ui.screens.info

import androidx.lifecycle.ViewModel
import com.davanok.electricitymeterhelper.domain.ReadingObjectsRepository
import kotlin.uuid.Uuid

class InfoViewModel(
    private val entryId: Uuid,
    private val repository: ReadingObjectsRepository
) : ViewModel() {
}