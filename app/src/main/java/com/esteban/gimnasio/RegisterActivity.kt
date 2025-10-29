package com.esteban.gimnasio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


// Asume que tienes las clases User y AppDatabase definidas y accesibles
// import com.tusdominio.tuapp.database.AppDatabase
// import com.tusdominio.tuapp.database.User

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }
}