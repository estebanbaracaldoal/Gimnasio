package com.esteban.gimnasio

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class WorkoutsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workouts)

        val backButton: Button = findViewById(R.id.btn_back)
        val profileButton: Button = findViewById(R.id.btn_profile)

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
