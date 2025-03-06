package com.example.normalapp.coordinators.dataCoordinator

import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.HttpHeaderParser
import com.example.normalapp.coordinators.dataCoordinator.DataCoordinator.Companion.identifier
import com.example.normalapp.models.api.SampleError
import com.example.normalapp.models.api.SampleResponse
import com.example.normalapp.models.constants.DebuggingIdentifiers
import com.example.normalapp.utils.data.GsonRequest
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

fun DataCoordinator.generateData(
    url: String,
    fakersCount: Int,
    onSuccess: () -> Unit,
    onError: () -> Unit
) {
    val apiRequestQueue = this.apiRequestQueue ?: return

    val payload = JSONObject()
    payload.put("fakers_count", fakersCount)

    var headers: MutableMap<String, String> = HashMap<String, String>()
    headers.put("a-header-key", "a-sample-key")

    val request = GsonRequest(
        url = url,
        clazz = SampleResponse::class.java,
        method = Request.Method.POST,
        headers = headers,
        jsonPayload = payload,
        listener = {
            Log.i(
                identifier,
                "${DebuggingIdentifiers.actionOrEventSucceded} request : $it.",
            )
            onSuccess()
        },
        errorListener = {
            val response = it.networkResponse
            try {
                val errorJson = String(
                    response.data,
                    Charset.forName(HttpHeaderParser.parseCharset(response.headers))
                )
                val errorObj = Gson().fromJson(errorJson, SampleError::class.java)
                Log.i(
                    identifier,
                    "${DebuggingIdentifiers.actionOrEventFailed} request : ${errorObj.error}",
                )
                onError()
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
            }
        })

    apiRequestQueue.add(request)
}