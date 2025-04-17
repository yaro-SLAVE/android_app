package com.example.normalapp.data.api.dataclasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class User(
    val id: Int,
    val username: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("first_name") val lastName: String
)
@Serializable
data class UserProfile(
    val id: Int,
    val user: User,
    @SerialName("birth_date") val dateBirth: Date
)

enum class UserApiError {
    USER_NOT_FOUND,
    UNKNOWN
}