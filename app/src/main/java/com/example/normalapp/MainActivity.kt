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
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class MainActivity : AppCompatActivity() {
    private val hostServer = DataCoordinator.shared.hostServer

    @Serializable
    data class GenerationDataApiRequest(
        @SerialName("fakers_count") val fakersCount: String
    )

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
                    val client = HttpClient {
                        install(ContentNegotiation) {
                            json()
                        }
                    }


                    val usersResult: Deferred<Boolean> = CoroutineScope(Dispatchers.IO).async {
                        val result = withContext(Dispatchers.IO) {
                            client.post("${hostServer}/api/generate_data/users/"){
                                contentType(ContentType.Application.Json)
                                setBody(MultiPartFormDataContent(formData {
                                    append("fakers_count", fakersCount.text.toString())
                                }))
                            }
                        }

                        return@async result.status == HttpStatusCode.OK
                    }

                    val basementsResult: Deferred<Boolean> = CoroutineScope(Dispatchers.IO).async {
                        val result = withContext(Dispatchers.IO) {
                            client.post("${hostServer}/api/generate_data/basements/"){
                                contentType(ContentType.Application.Json)
                                setBody(MultiPartFormDataContent(formData {
                                    append("fakers_count", fakersCount.text.toString())
                                }))
                            }
                        }

                        return@async result.status == HttpStatusCode.OK
                    }

                    runBlocking {
                        val finalResult = usersResult.await() && basementsResult.await()

                        if (finalResult) {
                            CoroutineScope(Dispatchers.IO).launch {
                                val childrenResult = withContext(Dispatchers.IO) {
                                    client.post("${hostServer}/api/generate_data/children/"){
                                        contentType(ContentType.Application.Json)
                                        setBody(MultiPartFormDataContent(formData {
                                            append("fakers_count", fakersCount.text.toString())
                                        }))
                                    }
                                }

                                if (childrenResult.status == HttpStatusCode.OK) {
                                    val postsResult = withContext(Dispatchers.IO) {
                                        client.post("${hostServer}/api/generate_data/posts/"){
                                            contentType(ContentType.Application.Json)
                                            setBody(MultiPartFormDataContent(formData {
                                                append("fakers_count", fakersCount.text.toString())
                                            }))
                                        }
                                    }
                                }
                            }
                        }
                    }
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
}