package com.example.normalapp

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.normalapp.coordinators.dataCoordinator.DataCoordinator
import com.example.normalapp.coordinators.dataCoordinator.DataCoordinator.Companion.identifier
import com.example.normalapp.coordinators.languageCoordinator.LanguageCoordinator
import com.example.normalapp.models.constants.DebuggingIdentifiers
import kotlinx.coroutines.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class MainActivity : AppCompatActivity() {
//    private val client = HttpC
    private val hostServer = DataCoordinator.shared.hostServer

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupCoordinators()

        println("host server - " + hostServer)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val basementButton = findViewById<Button>(R.id.basementButton)
        val generateButton = findViewById<ImageButton>(R.id.generateButton)

        val loginIntent = Intent(this, LoginActivity::class.java)
        val signUpIntent = Intent(this, RegisterActivity::class.java)
        val basementIntent = Intent(this, BasementActivity::class.java)

        loginButton.setOnClickListener {
            startActivity(loginIntent)
        }

        signUpButton.setOnClickListener {
            startActivity(signUpIntent)
        }

        basementButton.setOnClickListener {
            startActivity(basementIntent)
        }

        generateButton.setOnClickListener {
            val alert: AlertDialog.Builder = AlertDialog.Builder(this)

            alert.setTitle("Генерация данных")
            alert.setMessage("Укажите число генерируемых пользователей")

            val fakersCount = EditText(this)
            fakersCount.inputType = InputType.TYPE_CLASS_NUMBER
            fakersCount.id = View.generateViewId()

            val mainLayout = ConstraintLayout(this)
            mainLayout.id = View.generateViewId()

            val fakersCountLayout = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )

            fakersCountLayout.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
            fakersCountLayout.topToTop = ConstraintLayout.LayoutParams.PARENT_ID

            fakersCount.layoutParams = fakersCountLayout

            mainLayout.addView(fakersCount)

            alert.setView(mainLayout)

            alert.setPositiveButton(
                "Сгенерировать",
                DialogInterface.OnClickListener { dialog, whichButton ->
/*
                    CoroutineScope(Dispatchers.Main).launch {
                        val usersResult = withContext(Dispatchers.IO) {
                            generateData("${hostServer}/api/generate_data/users/", fakersCount.text.toString())
                        }

                        val basementsResult = withContext(Dispatchers.IO) {
                            generateData("${hostServer}/api/generate_data/basements/", fakersCount.text.toString())
                        }


                    }

 */

                }
            )

            alert.setNegativeButton("Отмена",
                DialogInterface.OnClickListener { dialog, whichButton -> })

            alert.show()
        }
    }

    private fun setupCoordinators() {
        Log.i(
            identifier,
            "${DebuggingIdentifiers.actionOrEventInProgress} Setting Up Coordinators.",
        )
        LanguageCoordinator.shared.initialize(context = baseContext)
        DataCoordinator.shared.initialize(
            context = baseContext,
            onLoad = {},
        )
    }

    /*
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun generateData(url: String, fakersCount: String): Boolean {
        val body = FormBody.Builder()
            .add("fakers_count", fakersCount)
            .build()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .post(body)
            .build()

        return try {
            val response: Response = client.newCall(request).execute()
//            response.body?.toString()
            response.isSuccessful
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private suspend fun generateUsersAndBasements(fakersCount: String){
        val result = generateData("http://10.0.2.2:8000/api/generate_data/users/", fakersCount)
        generateData("http://10.0.2.2:8000/api/generate_data/basements/", fakersCount)

        println("result: $result")

        delay(60 * 1000)
    }

     */
}