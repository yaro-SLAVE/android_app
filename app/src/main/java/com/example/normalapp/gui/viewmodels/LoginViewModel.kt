package com.example.normalapp.gui.viewmodels

import android.app.Application
import android.text.Editable
import androidx.lifecycle.AndroidViewModel
import com.example.normalapp.data.api.ApiResult
import com.example.normalapp.data.api.services.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application,
    private val authService: AuthService
): AndroidViewModel(application) {
    suspend fun login(username: String, password: String) {

        when (val result = authService.login(username, password)) {
            is ApiResult.Error -> {
            }

            is ApiResult.Success -> {
            }

            is ApiResult.UnrecoverableError -> TODO()
        }
    }
}