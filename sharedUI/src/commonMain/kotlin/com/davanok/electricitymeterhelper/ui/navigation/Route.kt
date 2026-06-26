package com.davanok.electricitymeterhelper.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Home : Route
    @Serializable
    data class Info(val id: Int) : Route
    @Serializable
    data object Reading : Route
}