package com.example.normalapp.gui.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.normalapp.data.api.ApiResult
import com.example.normalapp.data.api.services.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application,
    private val authService: AuthService
): AndroidViewModel(application) {

    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    fun login() {
        println("fun has called")
        val user = username.value
        val pass = password.value
        if (user != null && pass != null) {
            CoroutineScope(Dispatchers.IO).launch {
                when (val result = authService.login(user, pass)) {
                    is ApiResult.Error -> {

                    }

                    is ApiResult.Success -> {
                        println(result.successValue.access)
                    }

                    is ApiResult.UnrecoverableError -> TODO()
                }
            }
        } else {
            println("string is null")
        }
    }
}