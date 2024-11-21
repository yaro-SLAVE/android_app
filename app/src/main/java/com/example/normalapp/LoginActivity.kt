package com.example.normalapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File

class LoginActivity : AppCompatActivity() {

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

        val db = baseContext.openOrCreateDatabase("app.db", MODE_PRIVATE, null)

        submitButton.setOnClickListener {
            if (usernameText.text.toString() != "" && usernameText.text.toString() != "Login" && passwordText.text.toString() != "") {
                var flag = false

                val username = usernameText.text.toString()
                val password = passwordText.text.toString()

                val selectionArgs = arrayOf<String>(java.lang.String.valueOf(username))

                val query = db.query("users", null, "login = ?", selectionArgs, null, null, null)

                while (query.moveToNext()) {
                    if (query.getString(2) == password) {
                        flag = true
                    }
                }

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
}