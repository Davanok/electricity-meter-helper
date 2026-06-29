package com.davanok.electricitymeterhelper.domain

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class ReadingObjectMin(
    val id: Uuid,
    val date: LocalDateTime,
)