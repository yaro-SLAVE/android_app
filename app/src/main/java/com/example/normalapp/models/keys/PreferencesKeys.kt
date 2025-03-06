package com.example.normalapp.models.keys

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val jwt = stringPreferencesKey("jwt")
    val refreshToken = stringPreferencesKey("refreshToken")
    val username = stringPreferencesKey("username")
    val firstName = stringPreferencesKey("firstName")
    val lastName = stringPreferencesKey("lastName")
    val birthDate = stringPreferencesKey("birthDate")
}