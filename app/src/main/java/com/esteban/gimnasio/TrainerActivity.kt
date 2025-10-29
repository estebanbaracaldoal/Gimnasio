package com.esteban.gimnasio

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class TrainerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trainer)

        val backButton: Button = findViewById(R.id.button_back)
        backButton.setOnClickListener {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}