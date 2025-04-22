package com.example.normalapp.data.api.services

import com.example.normalapp.coordinators.dataCoordinator.DataCoordinator
import com.example.normalapp.data.api.ApiResult
import com.example.normalapp.data.api.ErrorResponse
import com.example.normalapp.data.api.ErrorResponseType
import com.example.normalapp.data.api.dataclasses.RegisterApiError
import com.example.normalapp.data.api.dataclasses.RegisterRequest
import com.example.normalapp.data.api.httpExceptionWrap
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class UserProfileService @Inject constructor(
    private val httpClient: HttpClient
){
    private val hostServer: String = DataCoordinator.shared.hostServer
    private val registerUrl: String = hostServer + "/api/profile/"

    suspend fun register(username: String, password: String, firstName: String, lastName: String, birthDate: String = ""): ApiResult<Unit, RegisterApiError> = httpExceptionWrap(
        RegisterApiError.UNKNOWN) {
        val response = httpClient.post(registerUrl) {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    username = username,
                    password = password,
                    firstName = firstName,
                    lastName = lastName,
                    birthDate = birthDate
                )
            )
        }

        return@httpExceptionWrap if (response.status.value in 200..299)
            ApiResult.Success(Unit)
        else when(response.body<ErrorResponse>().error) {
            ErrorResponseType.USER_EXISTS -> ApiResult.Error(RegisterApiError.USER_EXISTS)
            else -> ApiResult.Error(RegisterApiError.UNKNOWN)
        }
    }
}