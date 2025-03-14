package com.example.normalapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.normalapp.coordinators.dataCoordinator.DataCoordinator
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readBytes
import io.ktor.client.statement.readText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.withContext


class LoginActivity : AppCompatActivity() {
    private val hostServer = DataCoordinator.shared.hostServer

    val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val basementButton = findViewById<Button>(R.id.basementButton)
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val homeButton = findViewById<Button>(R.id.homeButton)
        val submitButton = findViewById<Button>(R.id.loginSubmitButton)

        val usernameText = findViewById<EditText>(R.id.usernameEdit)
        val passwordText = findViewById<EditText>(R.id.passwordEdit)

        val notification = findViewById<TextView>(R.id.loginText)

        val basementIntent = Intent(this, BasementActivity::class.java)
        val signUpIntent = Intent(this, RegisterActivity::class.java)
        val homeIntent = Intent(this, MainActivity::class.java)

        appSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

        submitButton.setOnClickListener {
            if (usernameText.text.toString() != "" && usernameText.text.toString() != "Login" && passwordText.text.toString() != "") {
                val username = usernameText.text.toString()
                val password = passwordText.text.toString()

                var flag = false

                val loginThread = Thread{ Runnable {

//                    val loginResponse = login(username, password)
                }}

                if (flag) {
                    notification.text = usernameText.text.toString() + ", вы успешно вошли в аккаунт!"
                    notification.setTextColor(Color.BLUE)

                    usernameText.setText("Login")
                    passwordText.setText("")
                } else {
                    notification.text = "Вы неверно ввели логин и/или пароль"
                    notification.setTextColor(Color.RED)
                }
            } else {
                notification.text = "Вы не ввели логин и/или пароль"
                notification.setTextColor(Color.RED)
            }
        }

        basementButton.setOnClickListener {
            startActivity(basementIntent)
        }

        signUpButton.setOnClickListener {
            startActivity(signUpIntent)
        }

        homeButton.setOnClickListener {
            startActivity(homeIntent)
        }
    }

    private suspend fun login(username: String, password: String): String {
        val response =  client.post("${hostServer}/api/generate_data/users/"){
            contentType(ContentType.Application.Json)
            setBody(MultiPartFormDataContent(formData {
                append("username", username)
                append("password", password)
            }))
        }

        return response.body()
    }
}