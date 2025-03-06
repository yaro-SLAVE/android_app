package com.example.normalapp.coordinators.dataCoordinator

import android.util.Log
import androidx.datastore.preferences.core.edit
import com.example.normalapp.coordinators.languageCoordinator.LanguageCoordinator.Companion.identifier
import com.example.normalapp.models.constants.DebuggingIdentifiers
import com.example.normalapp.models.keys.PreferencesKeys
import kotlinx.coroutines.flow.firstOrNull

suspend fun DataCoordinator.getJwtDataStore(): String {
    val context = this.context ?: return defaultJwt
    return context.dataStore.data.firstOrNull()?.get(PreferencesKeys.jwt)
        ?: defaultJwt
}

suspend fun DataCoordinator.setJwtDataStore(value: String) {
    val context = this.context ?: return
    Log.i(
        identifier,
        "${DebuggingIdentifiers.actionOrEventInProgress} setSampleDataStore  ${DebuggingIdentifiers.actionOrEventInProgress}."
    )
    context.dataStore.edit { preferences ->
        preferences[PreferencesKeys.jwt] = value
        Log.i(
            identifier,
            "${DebuggingIdentifiers.actionOrEventInProgress} setSampleDataStore  ${DebuggingIdentifiers.actionOrEventSucceded} sample string : $value."
        )
    }
}

suspend fun DataCoordinator.getRefreshTokenDataStore(): String {
    val context = this.context ?: return defaultRefreshToken
    return context.dataStore.data.firstOrNull()?.get(PreferencesKeys.refreshToken)
        ?: defaultRefreshToken
}

suspend fun DataCoordinator.setRefreshTokenDataStore(value: String) {
    val context = this.context ?: return
    Log.i(
        identifier,
        "${DebuggingIdentifiers.actionOrEventInProgress} setSampleDataStore  ${DebuggingIdentifiers.actionOrEventInProgress}."
    )
    context.dataStore.edit { preferences ->
        preferences[PreferencesKeys.refreshToken] = value
        Log.i(
            identifier,
            "${DebuggingIdentifiers.actionOrEventInProgress} setSampleDataStore  ${DebuggingIdentifiers.actionOrEventSucceded} sample string : $value."
        )
    }
}

suspend fun DataCoordinator.getUsernameDataStore(): String {
    val context = this.context ?: return defaultUsername
    return context.dataStore.data.firstOrNull()?.get(PreferencesKeys.username)
        ?: defaultUsername
}

suspend fun DataCoordinator.setUsernameDataStore(value: String) {
    val context = this.context ?: return
    Log.i(
        identifier,
        "${DebuggingIdentifiers.actionOrEventInProgress} setSampleDataStore  ${DebuggingIdentifiers.actionOrEventInProgress}."
    )
    context.dataStore.edit { preferences ->
        preferences[PreferencesKeys.username] = value
        Log.i(
            identifier,
            "${DebuggingIdentifiers.actionOrEventInProgress} setSampleDataStore  ${DebuggingIdentifiers.actionOrEventSucceded} sample string : $value."
        )
    }
}