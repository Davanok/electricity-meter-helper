package com.davanok.electricitymeterhelper.ui.navigation

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
sealed interface Route {
    @Serializable
    data object Home : Route
    @Serializable
    data class Info(val id: Uuid) : Route
    @Serializable
    data class Reading(val entryId: Uuid?) : Route
}