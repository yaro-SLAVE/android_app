package com.example.normalapp.data.api.services

import com.example.normalapp.coordinators.dataCoordinator.DataCoordinator
import com.example.normalapp.data.api.ApiResult
import com.example.normalapp.data.api.ErrorResponse
import com.example.normalapp.data.api.ErrorResponseType
import com.example.normalapp.data.api.UnrecoverableErrorType
import com.example.normalapp.data.api.dataclasses.LoginApiError
import com.example.normalapp.data.api.dataclasses.LoginRequest
import com.example.normalapp.data.api.dataclasses.LogoutApiError
import com.example.normalapp.data.api.dataclasses.RefreshApiError
import com.example.normalapp.data.api.dataclasses.RefreshRequest
import com.example.normalapp.data.api.httpExceptionWrap
import com.example.normalapp.data.api.mapErr
import com.example.normalapp.data.api.wrappers.AuthWrapperError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class AuthService @Inject constructor(
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

    suspend fun refreshTokens(refresh: String): ApiResult<Unit, RefreshApiError> = httpExceptionWrap(RefreshApiError.UNKNOWN) {
        val response = httpClient.post(refreshUrl) {
            contentType(ContentType.Application.Json)
            setBody(
                RefreshRequest(
                    refresh = refresh
                )
            )
        }

        return@httpExceptionWrap if (response.status.value in 200..299)
            ApiResult.Success(Unit)
        else when (response.body<ErrorResponse>().error) {
            ErrorResponseType.REFRESH_INVALID -> ApiResult.Error(RefreshApiError.INVALID)
            else -> ApiResult.Error(RefreshApiError.UNKNOWN)
        }
    }

    suspend fun logout(): ApiResult<Unit, LogoutApiError> = httpExceptionWrap(LogoutApiError.UNKNOWN) {
        val response = httpClient.get(logoutUrl)

        return@httpExceptionWrap if (response.status.value in 200..299)
            ApiResult.Success(Unit)
        else when(response.body<ErrorResponse>().error) {
            ErrorResponseType.REFRESH_INVALID, ErrorResponseType.REFRESH_NOT_FOUND -> ApiResult.Error(LogoutApiError.REFRESH_INVALID)
            else -> ApiResult.Error(LogoutApiError.UNKNOWN)
        }
    }

    suspend fun<T, E> authWrap(unknownError: E, inner: suspend () -> ApiResult<T, AuthWrapperError<E>>): ApiResult<T, E> = httpExceptionWrap(unknownError) {
        val refreshToken = DataCoordinator.shared.refreshToken

        for (i in 0 ..<3) {
            val resultInner = inner()

            return@httpExceptionWrap if (resultInner !is ApiResult.Error || resultInner.errorValue is AuthWrapperError.InnerError) {
                resultInner.mapErr { (it as AuthWrapperError.InnerError).innerError }
            } else {
                when (val resultRefresh = refreshTokens(refreshToken)) {
                    is ApiResult.UnrecoverableError -> ApiResult.UnrecoverableError(resultRefresh.unrecoverableType)
                    is ApiResult.Error -> when (resultRefresh.errorValue) {
                        RefreshApiError.INVALID -> break
                        RefreshApiError.UNKNOWN -> ApiResult.UnrecoverableError(UnrecoverableErrorType.OTHER_UNRECOVERABLE)
                    }

                    is ApiResult.Success -> continue
                }
            }
        }

        return@httpExceptionWrap ApiResult.UnrecoverableError(UnrecoverableErrorType.REFRESH_INVALID)
    }
}