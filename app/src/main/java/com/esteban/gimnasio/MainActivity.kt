package com.esteban.gimnasio

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.esteban.gimnasio.data.MyRoomDatabase
import com.esteban.gimnasio.data.dao.GimnasioDao
import com.esteban.gimnasio.data.entities.GimnasioEntity
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
        rememberMeCheckbox = findViewById(R.id.checkbox_remember)
        val loginButton: Button = findViewById(R.id.login_button)
        createAccountTextView = findViewById(R.id.text_create_account)

        val db = MyRoomDatabase.getDatabase(applicationContext)
        gimnasioDao = db.gimnasioDao()

        insertInitialUsers()
        loadSavedCredentialsRM()
        loadRegisterUsernameAndPassword()

        loginButton.setOnClickListener { loginUser() }

        createAccountTextView.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser() {
        val usernameInput = usernameEditText.text.toString()
        val passwordInput = passwordEditText.text.toString()
        val rememberMe = rememberMeCheckbox.isChecked

        if (usernameInput.isBlank() || passwordInput.isBlank()) {
            Toast.makeText(this, "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val user = gimnasioDao.getUserByCredentials(usernameInput, passwordInput)
            withContext(Dispatchers.Main) {
                if (user != null) {
                    val sharedPref = getSharedPreferences("loginPrefs", MODE_PRIVATE)
                    sharedPref.edit {
                        putString("active_username", user.username) // Siempre original
                        if (rememberMe) {
                            putString("username", user.username)
                            putString("password", passwordInput)
                        } else {
                            remove("username")
                            remove("password")
                        }
                        apply()
                    }

                    val intent = Intent(this@MainActivity, WorkoutsActivity::class.java)
                    intent.putExtra(WorkoutsActivity.USER_ROLE_KEY, user.rememberMe.lowercase(Locale.ROOT))
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@MainActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun insertInitialUsers() {
        lifecycleScope.launch(Dispatchers.IO) {
            val count = gimnasioDao.countUsers()
            if (count == 0L) {
                gimnasioDao.insertUser(
                    GimnasioEntity(
                        username = "admin",
                        password = "admin",
                        firstName = "Admin",
                        lastName = "Master",
                        email = "admin@admin.com",
                        dateBirth = "2025-10-31",
                        rememberMe = "admin"
                    )
                )
                gimnasioDao.insertUser(
                    GimnasioEntity(
                        username = "user",
                        password = "user",
                        firstName = "User",
                        lastName = "Regular",
                        email = "user@user.com",
                        dateBirth = "2025-10-31",
                        rememberMe = "user"
                    )
                )
            }
        }
    }

    private fun loadSavedCredentialsRM() {
        val sharedPref = getSharedPreferences("loginPrefs", MODE_PRIVATE)
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

        if (!registerUsername.isNullOrEmpty() && !registerPassword.isNullOrEmpty()) {
            usernameEditText.setText(registerUsername)
            passwordEditText.setText(registerPassword)
            Toast.makeText(this, "Registro exitoso. Inicie sesión.", Toast.LENGTH_SHORT).show()
        }
    }
}
