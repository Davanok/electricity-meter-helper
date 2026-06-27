package com.davanok.electricitymeterhelper.domain

import kotlinx.serialization.Serializable

@Serializable
data class Apartment(
    val name: String,
    val owner: String
)
