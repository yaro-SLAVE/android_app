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

class LoginActivity : AppCompatActivity() {
    private var usersList: ArrayList<Array<String?>> = ArrayList()

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

        val arguments: Bundle? = intent.extras

        if (arguments?.getString("newUsername") != null && arguments.getString("newPassword") != null) {
            val args = arrayOf(arguments.getString("newUsername"), arguments.getString("newPassword"))

            var flag = false
            for (user in usersList) {
                if (user[0] == args[0]) {
                    flag = true
                }
            }

            if (!flag) {
                usersList.add(args)
            }
        }

        submitButton.setOnClickListener {
            var flag = false

            if (usernameText.text.toString() != "" && passwordText.text.toString() != "") {
                for (user in usersList) {
                    if (user[0] == usernameText.text.toString() && user[1] == passwordText.text.toString()) {
                        flag = true
                        break
                    }
                }
            } else {
                notification.text = "Вы не ввели логин и/или пароль"
                notification.setTextColor(Color.RED)
            }

            if (flag) {
                notification.text = usernameText.text.toString() + ", вы успешно вошли в аккаунт!"
                notification.setTextColor(Color.BLUE)

                homeIntent.putExtra("username", usernameText.text.toString())

                usernameText.setText("Login")
                passwordText.setText("")
            } else {
                notification.text = "Вы неверно ввели логин и/или пароль"
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