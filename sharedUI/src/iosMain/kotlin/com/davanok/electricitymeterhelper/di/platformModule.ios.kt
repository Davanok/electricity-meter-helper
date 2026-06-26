package com.davanok.electricitymeterhelper.di

import com.davanok.electricitymeterhelper.data.DataStorage
import com.davanok.electricitymeterhelper.data.FilesStorage
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.files.Path
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

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
@OptIn(ExperimentalForeignApi::class)
private fun provideDataDir(): String =
    NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        appropriateForURL = null,
        create = false,
        inDomain = NSUserDomainMask,
        error = null
    )!!.path!!