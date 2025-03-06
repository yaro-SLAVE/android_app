package com.example.normalapp.coordinators.dataCoordinator

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun DataCoordinator.updateJwt(value: String) {
    this.jwt = value
    GlobalScope.launch(Dispatchers.Default) {
        setJwtDataStore(value)
    }
}

fun DataCoordinator.updateRefreshToken(value: String) {
    this.refreshToken = value
    GlobalScope.launch(Dispatchers.Default) {
        setRefreshTokenDataStore(value)
    }
}

fun DataCoordinator.updateUsername(value: String) {
    this.username = value
    GlobalScope.launch(Dispatchers.Default) {
        setUsernameDataStore(value)
    }
}