package com.example.normalapp.data.api.dataclasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class AuthRefreshResponse(
    val access: String,
    val refresh: String
)

@Serializable
data class RefreshRequest(
    val refresh: String
)

enum class LoginApiError {
    INCORRECT,
    UNKNOWN
}

enum class RefreshApiError {
    INVALID,
    UNKNOWN
}

enum class LogoutApiError {
    REFRESH_INVALID,
    UNKNOWN
}