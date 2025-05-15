package com.example.normalapp.gui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.normalapp.coordinators.dataCoordinator.DataCoordinator
import com.example.normalapp.coordinators.dataCoordinator.setJwtDataStore
import com.example.normalapp.coordinators.dataCoordinator.setRefreshTokenDataStore
import com.example.normalapp.data.api.ApiResult
import com.example.normalapp.data.api.services.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application,
    private val authService: AuthService
): AndroidViewModel(application) {

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    fun login() {
        val user = username.value
        val pass = password.value
        CoroutineScope(Dispatchers.IO).launch {
            if (user != null && pass != null) {
                when (val result = authService.login(user, pass)) {
                    is ApiResult.Error -> {
                        _toastMessage.emit(result.errorValue.name)
                    }

                    is ApiResult.Success -> {
                        DataCoordinator.shared.setJwtDataStore(result.successValue.access)
                        DataCoordinator.shared.setRefreshTokenDataStore(result.successValue.refresh)
                        _toastMessage.emit("Вы успешно авторизовались")
                    }

                    is ApiResult.UnrecoverableError -> TODO()
                }
            } else {
                _toastMessage.emit("Вы не ввели логин или пароль")
            }
        }
    }
}