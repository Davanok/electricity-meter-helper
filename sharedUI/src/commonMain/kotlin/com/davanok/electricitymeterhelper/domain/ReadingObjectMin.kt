package com.davanok.electricitymeterhelper.domain

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class ReadingObjectMin(
    val id: Uuid,
    val date: LocalDate,
)