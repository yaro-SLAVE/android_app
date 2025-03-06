package com.example.normalapp.coordinators.dataCoordinator

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.preferencesDataStore
import com.example.normalapp.models.constants.DebuggingIdentifiers
class DataCoordinator {
    companion object {
        val shared = DataCoordinator()
        const val identifier = "[DataCoordinator]"
    }
    // MARK: Variables
    var context: Context? = null
    // Create a variable for each preference, along with a default value.
    // This is to guarantee that if it can't find it it resets to a value that you can control.
    /// Sample String
    var sampleStringPreferenceVariable: String = ""
    val defaultSampleStringPreferenceValue: String = ""
    /// Sample Int
    var sampleIntPreferenceVariable: Int = 0
    val defaultSampleIntPreferenceVariable: Int = 0
    /// Sample Boolean
    var sampleBooleanPreferenceVariable:  Boolean = false
    val defaultSampleBooleanPreferenceVariable: Boolean = false

    // MARK: Data Store Variables
    private val USER_PREFERENCES_NAME = "user_preferences"
    val Context.dataStore by preferencesDataStore(
        name = USER_PREFERENCES_NAME
    )

    // MARK: Lifecycle
    fun initialize(context: Context, onLoad: () -> Unit) {
        Log.i(
            "${DataCoordinator.identifier}",
            "${DebuggingIdentifiers.actionOrEventInProgress} initialize  ${DebuggingIdentifiers.actionOrEventInProgress}."
        )
        // Set Context
        this.context = context
        // Callback
        onLoad()
    }
}