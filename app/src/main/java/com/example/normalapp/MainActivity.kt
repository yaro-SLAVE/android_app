package com.example.normalapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.normalapp.coordinators.dataCoordinator.DataCoordinator
import com.example.normalapp.coordinators.dataCoordinator.DataCoordinator.Companion.identifier
import com.example.normalapp.coordinators.languageCoordinator.LanguageCoordinator
import com.example.normalapp.models.constants.DebuggingIdentifiers

class MainActivity : AppCompatActivity() {


    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupCoordinators()

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