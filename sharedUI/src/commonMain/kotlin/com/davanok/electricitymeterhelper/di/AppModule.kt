package com.davanok.electricitymeterhelper.di

import com.davanok.electricitymeterhelper.data.ReadingObjectsRepositoryImpl
import com.davanok.electricitymeterhelper.data.SaveReadingObjectRepositoryImpl
import com.davanok.electricitymeterhelper.domain.ReadingObjectsRepository
import com.davanok.electricitymeterhelper.domain.SaveReadingObjectRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun appModule() = module {
    single<ReadingObjectsRepository> {
        ReadingObjectsRepositoryImpl(get())
    }
    singleOf<SaveReadingObjectRepository>(::SaveReadingObjectRepositoryImpl)
}