package com.davanok.electricitymeterhelper.domain

import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class ReadingObject(
    val id: Uuid,
    val date: Instant,
    val entries: List<ReadingEntry>
)