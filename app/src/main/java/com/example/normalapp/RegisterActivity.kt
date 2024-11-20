package com.example.normalapp

import android.annotation.SuppressLint
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

class RegisterActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
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

        val notification = findViewById<TextView>(R.id.registerText)

        val basementIntent = Intent(this, BasementActivity::class.java)
        val loginIntent = Intent(this, LoginActivity::class.java)
        val homeIntent = Intent(this, MainActivity::class.java)

        submitButton.setOnClickListener {

            if (usernameText.text.toString() != "" && passwordText.text.toString() != "") {
                notification.text = "Вы успешно зарегистрировались!"
                notification.setTextColor(Color.BLUE)

                loginIntent.putExtra("newUsername", usernameText.text.toString())
                loginIntent.putExtra("newPassword", passwordText.text.toString())

                usernameText.setText("Login")
                passwordText.setText("")
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