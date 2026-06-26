package com.davanok.electricitymeterhelper.di

import com.davanok.electricitymeterhelper.data.DataStorage
import com.davanok.electricitymeterhelper.data.WebStorage
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<DataStorage> {
        WebStorage(
            "re",
            Json
        )
    }
}