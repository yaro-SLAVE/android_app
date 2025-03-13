package com.example.normalapp.coordinators.dataCoordinator

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.preferencesDataStore
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.example.normalapp.models.constants.DebuggingIdentifiers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DataCoordinator {
    companion object {
        val shared = DataCoordinator()
        const val identifier = "[DataCoordinator]"
    }
    var context: Context? = null

    var apiRequestQueue: RequestQueue? = null

    var hostServer: String = "http://10.0.2.2:8000"
    val defaultHostServer: String = "http://10.0.2.2:8000"

    var jwt: String = ""
    val defaultJwt: String = ""

    var refreshToken: String = ""
    val defaultRefreshToken: String = ""

    var username: String = ""
    val defaultUsername: String = ""

    var firstName: String = ""
    val defaultFirstName: String = ""

    var lastName: String = ""
    val defaultLastName: String = ""

    var birthDate: String = ""
    val defaultBirthDate: String = ""


    private val USER_PREFERENCES_NAME = "user_preferences"
    val Context.dataStore by preferencesDataStore(
        name = USER_PREFERENCES_NAME
    )

    fun initialize(context: Context, onLoad: () -> Unit) {
        Log.i(
            "${DataCoordinator.identifier}",
            "${DebuggingIdentifiers.actionOrEventInProgress} initialize  ${DebuggingIdentifiers.actionOrEventInProgress}."
        )

        this.context = context
        this.apiRequestQueue = Volley.newRequestQueue(context)

        GlobalScope.launch(Dispatchers.Default) {
            hostServer = getHostServerDataStore()
            jwt = getJwtDataStore()
            refreshToken = getRefreshTokenDataStore()
            username = getUsernameDataStore()

            Log.i(
                "${DataCoordinator.identifier}",
                "initialize  ${DebuggingIdentifiers.actionOrEventSucceded} String $jwt | String $refreshToken | String $username."
            )
            onLoad()
        }
    }
}