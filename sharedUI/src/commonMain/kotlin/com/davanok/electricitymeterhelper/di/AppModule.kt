package com.davanok.electricitymeterhelper.di

import com.davanok.electricitymeterhelper.data.ReadingObjectsRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun appModule() = module {
    singleOf(::ReadingObjectsRepositoryImpl)
}