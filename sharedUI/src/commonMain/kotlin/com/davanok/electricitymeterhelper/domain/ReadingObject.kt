package com.davanok.electricitymeterhelper.domain

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class ReadingObject(
    val id: Uuid,
    val date: LocalDate,
    val entries: List<ReadingEntry>
)