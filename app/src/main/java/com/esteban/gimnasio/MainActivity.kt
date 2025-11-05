package com.esteban.gimnasio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.esteban.gimnasio.data.MyRoomDatabase
import com.esteban.gimnasio.data.entities.GimnasioEntity
import com.esteban.gimnasio.data.dao.GimnasioDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var rememberMeCheckbox: CheckBox
    private lateinit var createAccountTextView: TextView
    private lateinit var gimnasioDao: GimnasioDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameEditText = findViewById(R.id.edit_text_username)
        passwordEditText = findViewById(R.id.edit_text_password)
        val loginButton: Button = findViewById(R.id.login_button)
        rememberMeCheckbox = findViewById(R.id.checkbox_remember)
        createAccountTextView = findViewById(R.id.text_create_account)

        val dbInstance = MyRoomDatabase.getDatabase(applicationContext)
        gimnasioDao = dbInstance.gimnasioDao()

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val rememberMe = rememberMeCheckbox.isChecked

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor ingrese un nombre de usuario y contraseña", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val autentiUser = gimnasioDao.getUserByCredentials(username, password)

                        withContext(Dispatchers.Main) {
                            if (autentiUser != null) {

                                val sharedPref = getSharedPreferences("loginPrefs", MODE_PRIVATE)
                                if (rememberMe) {
                                    sharedPref.edit {
                                        putString("username", username)
                                        putString("password", password)
                                        remove("active_username")
                                    }
                                } else {
                                    sharedPref.edit {
                                        remove("username")
                                        remove("password")
                                    }
                                }

                                sharedPref.edit {
                                    putString("active_username", username)
                                    apply()  
									
                                }

                                val userRole = autentiUser.rememberMe.lowercase(Locale.getDefault())

                                val intent = Intent(this@MainActivity, WorkoutsActivity::class.java).apply {
                                    putExtra(WorkoutsActivity.USER_ROLE_KEY, username)
                                }

                                when (userRole) {
                                    "admin" -> {

                                        startActivity(intent)
                                        finish()
                                    }
                                    "user" -> {
                                        startActivity(intent)
                                        finish()
                                    }
                                    else -> {
                                        Toast.makeText(this@MainActivity, "usuario desconocido", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(this@MainActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, "Error en el sistema: ${e.message}", Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
        insertDatosUser()

        createAccountTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        loadRegisterUsernameAndPassword()
        loadSavedCredentialsRM()
    }

    private fun insertDatosUser() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val userCount = gimnasioDao.countUsers()

                if (userCount == 0L) {
                    Log.d("Database", "Base de datos vacía. Insertando usuarios iniciales.")

                    val adminUser = GimnasioEntity(
                        username = "admin",
                        password = "admin",
                        firstName = "Admin",
                        lastName = "Master",
                        email = "admin@admin.com",
                        dateBirth = "2025-10-31",
                        rememberMe = "admin"
                    )
                    gimnasioDao.insertUser(adminUser)
                    Log.d("Database", "Usuario Master creado")

                    val user = GimnasioEntity(
                        username = "user",
                        password = "user",
                        firstName = "User",
                        lastName = "Regular",
                        email = "user@user.com",
                        dateBirth = "2025-10-31",
                        rememberMe = "user"
                    )
                    gimnasioDao.insertUser(user)
                }
            } catch (e: Exception) {
                Log.e("Database", "Error en la verificación: ${e.message}", e)
            }
        }
    }
    private fun loadSavedCredentialsRM() {
        val sharedPref = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        val savedUsername = sharedPref.getString("username", null)
        val savedPassword = sharedPref.getString("password", null)

        if (!savedUsername.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
            usernameEditText.setText(savedUsername)
            passwordEditText.setText(savedPassword)
            rememberMeCheckbox.isChecked = true
        }

    }

    private fun loadRegisterUsernameAndPassword() {
        val registerUsername = intent.getStringExtra(RegisterActivity.REGIS_USERNAME)
        val registerPassword = intent.getStringExtra(RegisterActivity.REGIS_PASSWORD)

        if (registerUsername != null && registerPassword != null) {
            usernameEditText.setText(registerUsername)
            passwordEditText.setText(registerPassword)

            Toast.makeText(this, "Registro exitoso.inicia sesion", Toast.LENGTH_SHORT).show()
            intent.removeExtra(RegisterActivity.REGIS_USERNAME)
            intent.removeExtra(RegisterActivity.REGIS_PASSWORD)
        }
    }
}