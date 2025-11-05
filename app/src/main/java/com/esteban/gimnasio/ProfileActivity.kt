package com.esteban.gimnasio

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.esteban.gimnasio.data.MyRoomDatabase
import com.esteban.gimnasio.data.dao.GimnasioDao
import com.esteban.gimnasio.data.entities.GimnasioEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var loginEditText: EditText
    private lateinit var nombreEditText: EditText
    private lateinit var apellidosEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var fechaNacEditText: EditText

    private lateinit var radioGroupTheme: RadioGroup
    private lateinit var spinnerLanguage: Spinner
    private lateinit var backButton: Button
    private lateinit var saveButton: Button
    private lateinit var gimnasioDao: GimnasioDao
    private lateinit var preferences: SharedPreferences
    private var currentUserId: Long = -1L 
    private var currentUserPassword: String = ""
    private val PREFS_FILE = "profilePrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val db = MyRoomDatabase.getDatabase(applicationContext)
        gimnasioDao = db.gimnasioDao()
        preferences = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)


        setupViews()
        loadUserProfile()
        setupListeners()
        loadPreferences()


    }

    private fun setupViews() {
        loginEditText = findViewById(R.id.edit_text_login)
        nombreEditText = findViewById(R.id.edit_text_nombre)
        apellidosEditText = findViewById(R.id.edit_text_apellidos)
        emailEditText = findViewById(R.id.edit_text_email)
        fechaNacEditText = findViewById(R.id.edit_text_fecha_nac)

        radioGroupTheme = findViewById(R.id.radio_group_theme)
        spinnerLanguage = findViewById(R.id.spinner_language)
        saveButton = findViewById(R.id.button_save_profile)
        backButton = findViewById(R.id.button_back_profile)

        loginEditText.isEnabled = false
    }

    private fun setupListeners() {
        saveButton.setOnClickListener {
            saveUserProfile()
            finish()
        }


        radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_theme_light -> setAppTheme(AppCompatDelegate.MODE_NIGHT_NO, "light")
                R.id.radio_theme_dark -> setAppTheme(AppCompatDelegate.MODE_NIGHT_YES, "dark")
            }
        }

        spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLanguage = parent?.getItemAtPosition(position).toString()
                if (selectedLanguage.isNotBlank() && getLanguagePref() != selectedLanguage) {
                    setAppLanguage(selectedLanguage)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadUserProfile() {
        val loggedInUsername = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
            .getString("active_username", null)

        if (loggedInUsername == null) {
            Toast.makeText(this, "Error: Usuario no autenticado.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val user = gimnasioDao.getUserByUsername(loggedInUsername)

            withContext(Dispatchers.Main) {
                if (user != null) {
                    currentUserId = user.id
                    currentUserPassword = user.password 

                    loginEditText.setText(user.username)
                    nombreEditText.setText(user.firstName)
                    apellidosEditText.setText(user.lastName)
                    emailEditText.setText(user.email)
                    fechaNacEditText.setText(user.dateBirth)
                } else {
                    Toast.makeText(this@ProfileActivity, "Error al cargar perfil.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }


    }
    private fun saveUserProfile() {
        val newNombre = nombreEditText.text.toString().trim()
        val newApellidos = apellidosEditText.text.toString().trim()
        val newEmail = emailEditText.text.toString().trim()
        val newFechaNac = fechaNacEditText.text.toString().trim()
        val currentLogin = loginEditText.text.toString()

        if (newNombre.isEmpty() || newEmail.isEmpty() || newFechaNac.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedUser = GimnasioEntity(
            id = currentUserId,
            username = currentLogin,
            password = currentUserPassword,
            firstName = newNombre,
            lastName = newApellidos,
            email = newEmail,
            dateBirth = newFechaNac,
            rememberMe = "user"
        )

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                gimnasioDao.updateUser(updatedUser)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ProfileActivity, "Perfil actualizado con éxito.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ProfileActivity, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loadPreferences() {
        val savedTheme = preferences.getString("app_theme", "light")
        if (savedTheme == "dark") {
            radioGroupTheme.check(R.id.radio_theme_dark)
        } else {
            radioGroupTheme.check(R.id.radio_theme_light)
        }

        val savedLang = getLanguagePref()
        val langArray = resources.getStringArray(R.array.language_options)
        val position = langArray.indexOfFirst { it.contains(savedLang, ignoreCase = true) }
        if (position >= 0) {
            spinnerLanguage.setSelection(position)
        }
    }

    private fun setAppTheme(mode: Int, themeName: String) {
        preferences.edit().putString("app_theme", themeName).apply()

        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun getLanguagePref(): String {
        return preferences.getString("app_language", "Español") ?: "Español"
    }

    private fun setAppLanguage(languageName: String) {
        preferences.edit().putString("app_language", languageName).apply()

        val localeCode = when (languageName) {
            "Español" -> "es"
            "English" -> "en"
            else -> "en" 
        }
        val locale = Locale.forLanguageTag(localeCode)
        Locale.setDefault(locale)
        val configuration = resources.configuration
        configuration.setLocale(locale)

        val refreshIntent = Intent(this, ProfileActivity::class.java)
        refreshIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val options = ActivityOptions.makeCustomAnimation(
            this,
            android.R.anim.fade_in, 
            android.R.anim.fade_out
        )
        startActivity(refreshIntent, options.toBundle())
        finish()

    }
}
