package com.davanok.electricitymeterhelper.domain

import kotlinx.serialization.Serializable

@Serializable
data class ReadingEntry(
    val apartment: Apartment,
    val previousValue: Int,
    val currentValue: Int
)