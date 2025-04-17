package com.example.normalapp.data.api.dataclasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class Child(
    val id: Int,
    @SerialName("first_name") val firstName: String,
    val gender: String,
    @SerialName("birth_date") val birthDate: Date,
    val basement: Int
)