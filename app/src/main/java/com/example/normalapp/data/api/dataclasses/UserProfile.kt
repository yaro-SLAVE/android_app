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

@Serializable
data class RegisterRequest(
    val username: String,
    val password: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("birth_date") val birthDate: String
)

enum class UserApiError {
    USER_NOT_FOUND,
    UNKNOWN
}

enum class RegisterApiError {
    USER_EXISTS,
    UNKNOWN
}