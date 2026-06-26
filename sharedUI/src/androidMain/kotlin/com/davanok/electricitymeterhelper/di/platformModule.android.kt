package com.davanok.electricitymeterhelper.di

import android.content.Context
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
        val context: Context = get()
        FilesStorage(
            Path(context.dataDir.toString()),
            ProtoBuf,
            "binpb"
        )
    }
}