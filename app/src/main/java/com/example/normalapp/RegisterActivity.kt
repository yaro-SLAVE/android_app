package com.example.normalapp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.normalapp.coordinators.dataCoordinator.DataCoordinator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.newCoroutineContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.wait
import java.io.IOException
import java.util.Calendar


class RegisterActivity : AppCompatActivity() {
    private val hostServer = DataCoordinator.shared.hostServer
    private val username = DataCoordinator.shared.username
    private val jwt = DataCoordinator.shared.jwt
    private val refresh = DataCoordinator.shared.refreshToken

    var dateAndTime = Calendar.getInstance()

    private val client = OkHttpClient()

    @SuppressLint("MissingInflatedId", "SetTextI18n", "Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val loginButton = findViewById<Button>(R.id.loginButton)
        val basementButton = findViewById<Button>(R.id.basementButton)
        val homeButton = findViewById<Button>(R.id.homeButton)

        val submitButton = findViewById<Button>(R.id.registerSubmitButton)

        val usernameText = findViewById<EditText>(R.id.newUsernameEdit)
        val passwordText = findViewById<EditText>(R.id.newPasswordEdit)
        val firstNameText = findViewById<EditText>(R.id.firstNameText)
        val lastNameText = findViewById<EditText>(R.id.lastNameEdit)
        val birthDateText = findViewById<EditText>(R.id.birthDateEdit)

        val notification = findViewById<TextView>(R.id.registerText)

        val basementIntent = Intent(this, BasementActivity::class.java)
        val loginIntent = Intent(this, LoginActivity::class.java)
        val homeIntent = Intent(this, MainActivity::class.java)

        var d =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                dateAndTime.set(Calendar.YEAR, year)
                dateAndTime.set(Calendar.MONTH, monthOfYear)
                dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                birthDateText.setText(DateUtils.formatDateTime(
                    this,
                    dateAndTime.timeInMillis,
                    DateUtils.FORMAT_SHOW_YEAR
                ))
            }

        fun setDate() {
            DatePickerDialog(
                this@RegisterActivity, d,
                dateAndTime[Calendar.YEAR],
                dateAndTime[Calendar.MONTH],
                dateAndTime[Calendar.DAY_OF_MONTH]
            )
                .show()
        }

        birthDateText.setOnClickListener {
            setDate()
        }

        submitButton.setOnClickListener {

            if (usernameText.text.toString() != "" && passwordText.text.toString() != "") {
                val username = usernameText.text.toString()
                val password = passwordText.text.toString()
                val firstName = firstNameText.text.toString()
                val lastName = lastNameText.text.toString()
                val birthDate = birthDateText.text.toString()

                val registerResult: Deferred<Boolean> = CoroutineScope(Dispatchers.IO).async {

                        var flag = false
                        val mediaTypeJson = "application/json; charset=utf-8".toMediaType()
                        val jsonString = "{" +
                                "\"username\":\"${username}\"," +
                                "\"password\":\"${password}\"," +
                                "\"first_name\":\"${firstName}\"," +
                                "\"last_name\":\"${lastName}\"" +
                                "}"

                        println(jsonString)

                        val client = OkHttpClient()
                        val body: RequestBody = jsonString.toRequestBody(mediaTypeJson)
                        val request =
                            Request.Builder().url(hostServer + "/api/profile/").post(body).build()

                        client.newCall(request).execute().use { response ->
                            if (!response.isSuccessful) {
                                throw IOException(
                                    "Запрос к серверу не был успешен:" +
                                            " ${response.code} ${response.message}"
                                )
                                println(response.body!!.string())
                            }
                            println(response.body!!.string())
                            flag = response.isSuccessful
                        }

                    return@async flag
                }

                runBlocking {

                    if (registerResult.await()) {

                        val authResult = CoroutineScope(Dispatchers.IO).launch {
                            val mediaTypeJson = "application/json; charset=utf-8".toMediaType()
                            val jsonString = "{" +
                                    "\"username\":\"${username}\"," +
                                    "\"password\":\"${password}\"" +
                                    "}"
                            println(jsonString)

                            val client = OkHttpClient()
                            val body: RequestBody = jsonString.toRequestBody(mediaTypeJson)
                            val request =
                                Request.Builder().url(hostServer + "/api/auth/login/").post(body).build()

                            try {
                                client.newCall(request).execute().use { response ->
                                    if (!response.isSuccessful) {
                                        throw IOException(
                                            "Запрос к серверу не был успешен:" +
                                                    " ${response.code} ${response.message}"
                                        )
                                        println(response.body!!.string())
                                    }
                                    println(response.body!!.string())
                                }
                            } catch (e: IOException) {
                                println("Ошибка подключения: $e")
                            }
                        }
                    }
                }
            } else {
                notification.text = "Вы не ввели логин и/или пароль"
                notification.setTextColor(Color.RED)
            }
        }

        basementButton.setOnClickListener {
            startActivity(basementIntent)
        }

        loginButton.setOnClickListener {
            startActivity(loginIntent)
        }

        homeButton.setOnClickListener {
            startActivity(homeIntent)
        }
    }
}