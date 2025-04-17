package com.example.normalapp.data.api.dataclasses

import kotlinx.serialization.Serializable

@Serializable
data class Basement(
    val id: Int,
    val address: String,
    val capacity: Int
)
