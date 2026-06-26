package com.davanok.electricitymeterhelper.domain

import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class ReadingObjectMin(
    val id: Uuid,
    val date: Instant,
)