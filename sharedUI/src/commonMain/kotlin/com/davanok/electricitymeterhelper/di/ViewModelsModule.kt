package com.davanok.electricitymeterhelper.di

import com.davanok.electricitymeterhelper.ui.screens.home.HomeViewModel
import com.davanok.electricitymeterhelper.ui.screens.info.InfoViewModel
import com.davanok.electricitymeterhelper.ui.screens.reading.ReadingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import kotlin.uuid.Uuid

fun viewModelsModule() = module {
    viewModelOf(::HomeViewModel)
    viewModel<InfoViewModel> { (entryId: Uuid) -> InfoViewModel(entryId, get()) }
    viewModel<ReadingViewModel> { (entryId: Uuid?) -> ReadingViewModel(entryId, get()) }
}