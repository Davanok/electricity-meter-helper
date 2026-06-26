package com.davanok.electricitymeterhelper.di

import ElectricityMeterHelper.sharedUI.BuildConfig
import ca.gosyer.appdirs.AppDirs
import com.davanok.electricitymeterhelper.data.DataStorage
import com.davanok.electricitymeterhelper.data.FilesStorage
import kotlinx.io.files.Path
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import org.koin.core.module.Module
import org.koin.dsl.module

@OptIn(ExperimentalSerializationApi::class)
actual fun platformModule(): Module = module {
    single<DataStorage> {
        FilesStorage(
            Path(provideDataDir()),
            ProtoBuf,
            "binpb"
        )
    }
}
private val appDirs: AppDirs
    get() = AppDirs { appName = BuildConfig.APP_NAME }

fun provideDataDir(): String = appDirs.getUserDataDir()