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

class RegisterActivity : AppCompatActivity() {
    val usersFile = "users.bin"
    val usersFileSeparator = "---"
    val users: ArrayList<Array<String>> = ArrayList()

    val userConfigFile = "user_config.txt"

    @SuppressLint("MissingInflatedId", "SetTextI18n")
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

        var item = Array<String>(2) {""}
        var iter = 0

        openFileInput(usersFile).bufferedReader().useLines { lines ->
            for (line in lines) {
                if (line == usersFileSeparator) {
                    users.add(item)
                    item = arrayOf("", "")
                    iter = 0
                } else {
                    item[iter] = line.toString()
                    iter += 1
                }
            }
        }

        submitButton.setOnClickListener {

            if (usernameText.text.toString() != "" && passwordText.text.toString() != "") {
                if (!users.isEmpty()) {
                    var flag = false
                    for (user in users) {
                        if (user[0] == usernameText.text.toString()) {
                            flag = true
                            break
                        }
                    }

                    if (flag) {
                        notification.text = "Пользователь с таким логином уже существует!"
                        notification.setTextColor(Color.RED)
                    } else {
                        addUser(usernameText.text.toString(), passwordText.text.toString())

                        notification.text = "Вы успешно зарегистрировались!"
                        notification.setTextColor(Color.BLUE)

                        usernameText.setText("Login")
                        passwordText.setText("")
                    }
                } else {
                    addUser(usernameText.text.toString(), passwordText.text.toString())

                    notification.text = "Вы успешно зарегистрировались!"
                    notification.setTextColor(Color.BLUE)

                    usernameText.setText("Login")
                    passwordText.setText("")
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

    private fun addUser(username: String, password: String) {
        users.add(arrayOf(username, password))

        openFileOutput(usersFile, Context.MODE_PRIVATE).use {
            for (user in users) {
                it.write((user[0] + "\n").toByteArray())
                it.write((user[1] + "\n").toByteArray())
                it.write((usersFileSeparator + "\n").toByteArray())
            }
        }

        openFileOutput("$username.txt", Context.MODE_PRIVATE).use {
            it.write("0\n".toByteArray())
        }

        openFileOutput(userConfigFile, Context.MODE_PRIVATE).use {
            it.write("1\n".toByteArray())
            it.write(username.toByteArray())
        }
    }
}