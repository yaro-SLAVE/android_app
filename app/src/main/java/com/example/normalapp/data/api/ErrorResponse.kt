package com.example.normalapp.data.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
@Serializable
data class ErrorResponse(
    val error: ErrorResponseType,
    val message: String?,
    val description: String?
)

@Serializable
enum class ErrorResponseType {
    @SerialName("jwt-invalid") JWT_INVALID,
    @SerialName("refresh-invalid") REFRESH_INVALID,
    @SerialName("refresh-not-found") REFRESH_NOT_FOUND,
    @SerialName("account-already-exist") USER_EXISTS,
    @SerialName("bad-request") BAD_REQUEST,
    @SerialName("user-not-found") USER_NOT_FOUND,
    @SerialName("incorrect-password") INCORRECT_PASSWORD,
    @SerialName("usertag-or-password-incorrect") INCORRECT_LOGIN,
    @SerialName("unknown") UNKNOWN
}