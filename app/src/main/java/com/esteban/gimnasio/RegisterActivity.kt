package com.esteban.gimnasio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


// Asume que tienes las clases User y AppDatabase definidas y accesibles
// import com.tusdominio.tuapp.database.AppDatabase
// import com.tusdominio.tuapp.database.User

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val backButton: Button = findViewById(R.id.button_volver)

        backButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }
    }
}