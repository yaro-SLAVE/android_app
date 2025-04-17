package com.example.normalapp.data.api.repositories

import com.example.normalapp.coordinators.dataCoordinator.DataCoordinator
import com.example.normalapp.data.api.ApiResult
import com.example.normalapp.data.api.ErrorResponse
import com.example.normalapp.data.api.ErrorResponseType
import com.example.normalapp.data.api.dataclasses.LoginApiError
import com.example.normalapp.data.api.dataclasses.LoginRequest
import com.example.normalapp.data.api.httpExceptionWrap
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val httpClient: HttpClient
) {
    private val hostServer: String = DataCoordinator.shared.hostServer
    private val loginUrl: String = hostServer + "/api/auth/login/"
    private val refreshUrl: String = hostServer + "/api/auth/refresh/"
    private val logoutUrl: String = hostServer + "/api/auth/logout/"
    suspend fun login(username: String, password: String): ApiResult<Unit, LoginApiError> = httpExceptionWrap(LoginApiError.UNKNOWN) {
        val response = httpClient.post(loginUrl) {
            contentType(ContentType.Application.Json)
            setBody(
                LoginRequest(
                    username = username,
                    password = password
                )
            )
        }

        return@httpExceptionWrap if (response.status.value in 200..299)
            ApiResult.Success(Unit)
        else when(response.body<ErrorResponse>().error) {
            ErrorResponseType.INCORRECT_LOGIN -> ApiResult.Error(LoginApiError.INCORRECT)
            else -> ApiResult.Error(LoginApiError.UNKNOWN)
        }
    }
}