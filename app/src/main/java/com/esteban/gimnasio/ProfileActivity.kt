package com.esteban.gimnasio

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.esteban.gimnasio.data.MyRoomDatabase
import com.esteban.gimnasio.data.dao.GimnasioDao
import com.esteban.gimnasio.data.entities.GimnasioEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class ProfileActivity : AppCompatActivity() {

    private lateinit var editLogin: EditText
    private lateinit var editNombre: EditText
    private lateinit var editApellidos: EditText
    private lateinit var editEmail: EditText
    private lateinit var editFechaNac: EditText
    private lateinit var radioGroupTheme: RadioGroup
    private lateinit var radioLight: RadioButton
    private lateinit var radioDark: RadioButton
    private lateinit var spinnerLanguage: Spinner
    private lateinit var buttonSave: Button
    private lateinit var buttonBack: Button

    private lateinit var preferences: SharedPreferences
    private lateinit var gimnasioDao: GimnasioDao

    private val languages = arrayOf("english", "spanish")
    private var isSpinnerInitialized = false
    private var user: GimnasioEntity? = null

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("settings", MODE_PRIVATE)
        val lang = prefs.getString("app_language", "english") ?: "english"
        super.attachBaseContext(updateLocaleContext(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        preferences = getSharedPreferences("settings", MODE_PRIVATE)

        val isDarkMode = preferences.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val db = MyRoomDatabase.getDatabase(applicationContext)
        gimnasioDao = db.gimnasioDao()

        initViews()
        setupLanguageSpinner()
        setupThemeSelector()
        setupButtons()
        loadUserData()
    }

    private fun initViews() {
        editLogin = findViewById(R.id.edit_text_login)
        editNombre = findViewById(R.id.edit_text_nombre)
        editApellidos = findViewById(R.id.edit_text_apellidos)
        editEmail = findViewById(R.id.edit_text_email)
        editFechaNac = findViewById(R.id.edit_text_fecha_nac)
        radioGroupTheme = findViewById(R.id.radio_group_theme)
        radioLight = findViewById(R.id.radio_theme_light)
        radioDark = findViewById(R.id.radio_theme_dark)
        spinnerLanguage = findViewById(R.id.spinner_language)
        buttonSave = findViewById(R.id.button_save_profile)
        buttonBack = findViewById(R.id.button_back_profile)
        editLogin.isEnabled = false
    }

    private fun loadUserData() {
        val username =
            getSharedPreferences("loginPrefs", MODE_PRIVATE).getString("active_username", null)
                ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            val dbUser = gimnasioDao.getUserByUsername(username)
            withContext(Dispatchers.Main) {
                if (dbUser != null) {
                    user = dbUser
                    editLogin.setText(dbUser.username)
                    editNombre.setText(dbUser.firstName)
                    editApellidos.setText(dbUser.lastName)
                    editEmail.setText(dbUser.email)
                    editFechaNac.setText(dbUser.dateBirth)
                } else {
                    Toast.makeText(
                        this@ProfileActivity, "No se pudo cargar el perfil", Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }
    }

    private fun setupThemeSelector() {
        val isDarkMode = preferences.getBoolean("dark_mode", false)
        radioDark.isChecked = isDarkMode
        radioLight.isChecked = !isDarkMode

        radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_theme_light -> setAppTheme(false)
                R.id.radio_theme_dark -> setAppTheme(true)
            }
        }
    }

    private fun setAppTheme(darkMode: Boolean) {
        preferences.edit { putBoolean("dark_mode", darkMode) }
        AppCompatDelegate.setDefaultNightMode(
            if (darkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun setupLanguageSpinner() {
        val savedLang = preferences.getString("app_language", "english") ?: "english"

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLanguage.adapter = adapter

        val position = languages.indexOf(savedLang)
        if (position >= 0) spinnerLanguage.setSelection(position)

        spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long
            ) {
                if (!isSpinnerInitialized) {
                    isSpinnerInitialized = true
                    return
                }
                val selectedLanguage = languages[position]
                if (selectedLanguage != savedLang) {
                    preferences.edit { putString("app_language", selectedLanguage) }
                    recreate()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupButtons() {
        buttonSave.setOnClickListener {
            user?.let { u ->
                val newNombre = editNombre.text.toString()
                val newApellidos = editApellidos.text.toString()
                val newEmail = editEmail.text.toString()
                val newFecha = editFechaNac.text.toString()

                val updatedUser = u.copy(
                    firstName = newNombre,
                    lastName = newApellidos,
                    email = newEmail,
                    dateBirth = newFecha
                )

                lifecycleScope.launch(Dispatchers.IO) {
                    gimnasioDao.updateUser(updatedUser)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ProfileActivity, "Perfil guardado", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                }
            }
        }

        buttonBack.setOnClickListener {
            finish()
        }
    }

    private fun updateLocaleContext(base: Context, language: String): Context {
        val locale = Locale.forLanguageTag(
            when (language.lowercase(Locale.ROOT)) {
                "spanish" -> "es"
                else -> "en"
            }
        )
        val config = base.resources.configuration
        config.setLocales(android.os.LocaleList(locale))
        return base.createConfigurationContext(config)
    }
}

