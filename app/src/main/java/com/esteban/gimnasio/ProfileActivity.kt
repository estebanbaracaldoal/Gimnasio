package com.esteban.gimnasio

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class ProfileActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val backButton: Button = findViewById(R.id.btn_back_profile)

        backButton.setOnClickListener {
            val intent = Intent(this, WorkoutsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
