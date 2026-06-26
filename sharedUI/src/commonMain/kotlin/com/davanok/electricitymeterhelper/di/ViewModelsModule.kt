package com.davanok.electricitymeterhelper.di

import com.davanok.electricitymeterhelper.ui.screens.home.HomeViewModel
import com.davanok.electricitymeterhelper.ui.screens.info.InfoViewModel
import com.davanok.electricitymeterhelper.ui.screens.reading.ReadingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import kotlin.uuid.Uuid

val ViewModelsModule = module {
    viewModelOf(::HomeViewModel)
    viewModel<InfoViewModel> { (entryId: Uuid) -> InfoViewModel(entryId) }
    viewModel<ReadingViewModel> { (entryId: Uuid) -> ReadingViewModel(entryId) }
}