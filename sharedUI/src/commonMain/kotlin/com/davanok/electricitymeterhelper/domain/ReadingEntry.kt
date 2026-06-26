package com.davanok.electricitymeterhelper.domain

import kotlinx.serialization.Serializable

@Serializable
data class ReadingEntry(
    val apartment: Int,
    val owner: String,
    val previousValue: Int,
    val currentValue: Int
)