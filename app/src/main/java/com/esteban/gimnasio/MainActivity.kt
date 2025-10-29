package com.esteban.gimnasio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.esteban.gimnasio.data.MyRoomDatabase
import com.esteban.gimnasio.data.entities.EntiUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameEditText: EditText = findViewById(R.id.edit_text_username)
        val passwordEditText: EditText = findViewById(R.id.edit_text_password)
        val loginButton: Button = findViewById(R.id.button_login)
        val rememberMeCheckbox: CheckBox = findViewById(R.id.checkbox_remember)
        val createAccountTextView: TextView = findViewById(R.id.text_create_account)

        val db = MyRoomDatabase(this)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val rememberMe = rememberMeCheckbox.isChecked

            if(username.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Por favor ingrese un nombre de usuario y contraseña", Toast.LENGTH_SHORT).show()
            } else {
                try {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val autentiUser = db.userDao().getUserByCredentials(username, password)

                        withContext(Dispatchers.Main) {
                            if (autentiUser != null) {
                                Toast.makeText(this@MainActivity, "Bienvenido", Toast.LENGTH_SHORT).show()

                                val sharedPref = getSharedPreferences("loginPrefs", MODE_PRIVATE)
                                if(rememberMe){
                                    sharedPref.edit {
                                        putString("username", username)
                                        putString("password", password)
                                    }
                                } else {
                                    sharedPref.edit {
                                        clear()
                                    }
                                }

                                when(autentiUser.rememberMe.lowercase()){
                                    "admin" -> {
                                        val intent = Intent(this@MainActivity, TrainerActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    "user" -> {
                                        val intent = Intent(this@MainActivity, WorkoutsActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    else -> {
                                        Toast.makeText(this@MainActivity, "Tipo de usuario desconocido", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(this@MainActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Error en el sistema: ${e.message}", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }

        insertDatosUser(db)

        createAccountTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        loadSavedCredentialsRM()
    }
    private fun loadSavedCredentialsRM() {
        val usernameEditText: EditText = findViewById(R.id.edit_text_username)
        val passwordEditText: EditText = findViewById(R.id.edit_text_password)
        val rememberMeCheckbox: CheckBox = findViewById(R.id.checkbox_remember)

        val sharedPref = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        val savedUsername = sharedPref.getString("username", null)
        val savedPassword = sharedPref.getString("password", null)

        if (!savedUsername.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
            usernameEditText.setText(savedUsername)
            passwordEditText.setText(savedPassword)
            rememberMeCheckbox.isChecked = true
        }
    }
    private fun insertDatosUser(db: MyRoomDatabase) {
        lifecycleScope.launch(Dispatchers.IO) {
            val adminExists = db.userDao().getUserByLogin("admin")
            if (adminExists == null) {
                val adminUser = EntiUser(
                    username = "admin",
                    password = "admin",
                    firstName = "Admin",
                    lastName = "Admin",
                    email = "admin@admin.com",
                    dateBirth = "2025-10-31",
                    rememberMe = "admin"
                )
                val user = EntiUser(
                    username = "user",
                    password = "user",
                    firstName = "user",
                    lastName = "user",
                    email = "user@gimnasio.com",
                    dateBirth = "01/01/2000",
                    rememberMe = "user",
                )
                db.userDao().insertUser(adminUser)
                db.userDao().insertUser(user)
            }
        }
    }
}