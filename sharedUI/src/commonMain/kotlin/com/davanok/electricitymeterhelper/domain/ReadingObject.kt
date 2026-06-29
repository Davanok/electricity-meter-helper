package com.davanok.electricitymeterhelper.domain

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class ReadingObject(
    val id: Uuid,
    val date: LocalDateTime,
    val entries: List<ReadingEntry>
)