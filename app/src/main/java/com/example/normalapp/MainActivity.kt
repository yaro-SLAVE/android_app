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
import com.github.javafaker.Faker
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.readBytes
import io.ktor.client.statement.readText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentLength
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.InternalAPI
import io.ktor.util.toByteArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonArray

class MainActivity : AppCompatActivity() {
    private val hostServer = DataCoordinator.shared.hostServer
    private var isStartImitation: Boolean = false
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json{
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 240000
            connectTimeoutMillis = 20000
        }
    }


    private var thread: Job = CoroutineScope(Dispatchers.IO).launch {  }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        thread.cancel()

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
        val imitationButton = findViewById<Button>(R.id.imitationButton)

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

        imitationButton.setOnClickListener {
            if (isStartImitation) {
                imitationButton.text = "Начать имитацию"
                thread.cancel()
                isStartImitation = false
            } else {
                thread = startImitation()
                imitationButton.text = "Прекратить имитацию"
                isStartImitation = true
            }
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

    @OptIn(InternalAPI::class)
    private fun startImitation(): Job {

        var thread = CoroutineScope(Dispatchers.IO).launch {
            val profiles: String = client.get("${hostServer}/api/profile/").body()
            val posts: String = client.get("${hostServer}/api/post/").body()
            val basements: String = client.get("${hostServer}/api/basement/").body()

            val profilesList = profiles.split("{", "}")
            val postsList = posts.split("{", "}")
            val basementsList = posts.split("{", "}")

            var profilesCount = profilesList.size - 2
            var postsCount = postsList.size - 2
            var basementsCount = basementsList.size - 2

                for (i in 1..10) {
                    for (j in 1..10) {
                        client.get("${hostServer}/api/post/")
                        client.get("${hostServer}/api/profile/")
                        client.get("${hostServer}/api/basement/")
                        client.get("${hostServer}/api/user_basement/")
                        client.get("${hostServer}/api/child/")
                        client.get("${hostServer}/api/post_photo/")
                        client.get("${hostServer}/api/post_like/")
                        client.get("${hostServer}/api/child_photo/")
                        client.get("${hostServer}/api/comment/")
                        client.get("${hostServer}/api/comment_like/")
                        client.get("${hostServer}/api/reaction/")
                        client.get("${hostServer}/api/post_reaction/")
                    }

                    for (j in 1..3){
                        val user = (1..profilesCount).random()
                        val post = (1 .. postsCount).random()
                        val basement = (1 .. basementsCount).random()

                        val profileBody = client.get("${hostServer}/api/profile/${user}/").body<String>().split(",")
                        val postBody = client.get("${hostServer}/api/post/${post}/").body<String>().split(",")
                        val basementBody = client.get("${hostServer}/api/basement/${basement}/").body<String>().split(",")

                        if (client.get("${hostServer}/api/profile/${user}/").status == HttpStatusCode.OK) {
                            val result = client.put("${hostServer}/api/profile/${user}/") {
                                contentType(ContentType.Application.Json)
                                setBody(MultiPartFormDataContent(formData {
                                    append("user", profileBody[1].split(":")[1])
                                    append("logo", "")
                                    append("birth_date", "1993-10-4")

                                }))
                            }.body<String>()

                            println(result)
                        }

                        if (client.get("${hostServer}/api/post/${post}/").status == HttpStatusCode.OK) {
                            val result = client.put("${hostServer}/api/post/${post}/") {
                                contentType(ContentType.Application.Json)
                                setBody(MultiPartFormDataContent(formData {
                                    append("user", postBody[1].split(":")[1])
                                    append("title", "Измененный заголовок")
                                    append("body", postBody[3].split(":")[1])
                                    append("create_time", postBody[4].split("\":")[1].replace("}", "").replace("\"", ""))
                                }))
                            }.body<String>()

                            println(result)
                        }

                        if (client.get("${hostServer}/api/basement/${basement}/").status == HttpStatusCode.OK) {
                            val result = client.put("${hostServer}/api/basement/${basement}/") {
                                contentType(ContentType.Application.Json)
                                setBody(MultiPartFormDataContent(formData {
                                    append("address", basementBody[1].split(":")[1].replace("\"", ""))
                                    append("capacity", 30)
                                }))
                            }.body<String>()

                            println(result)
                        }

                        var postRequest = client.post("${hostServer}/api/profile/"){
                            contentType(ContentType.Application.Json)
                            setBody(MultiPartFormDataContent(formData{
                                append("username", "user${profilesCount * 11}")
                                append("password", "qwe123")
                                append("first_name", "Владимир")
                                append("last_name", "Ленин")
                                append("birth_date", "1993-10-4")
                            }))
                        }

                        ++profilesCount

                        client.delete("${hostServer}/api/profile/${(1 .. profilesCount).random()}/")

                        --profilesCount
                    }
                }
        }

        return thread
    }
}