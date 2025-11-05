package com.esteban.gimnasio

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.esteban.gimnasio.data.MyRoomDatabase
import com.esteban.gimnasio.data.entities.GimnasioEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale



class RegisterActivity : AppCompatActivity() {

    private lateinit var db: MyRoomDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        db = MyRoomDatabase.getDatabase(this)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        val loginEditText: EditText = findViewById(R.id.edit_text_reg_login)
        val passwordEditText: EditText = findViewById(R.id.edit_text_reg_pass)
        val nombreEditText: EditText = findViewById(R.id.edit_text_reg_nombre)
        val apellidosEditText: EditText = findViewById(R.id.edit_text_reg_apellidos)
        val emailEditText: EditText = findViewById(R.id.edit_text_reg_email)
        val fechaNacEditText: EditText = findViewById(R.id.edit_text_reg_fecha_nac)
        val tipoUsuarioSpinner: Spinner = findViewById(R.id.spinner_tipo_usuario)
        val registroButton: Button = findViewById(R.id.button_registro_confirm)
        val volverButton: Button = findViewById(R.id.button_volver)



        setupDatePicker(fechaNacEditText)

        registroButton.setOnClickListener {
            val username = loginEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val firstName = nombreEditText.text.toString().trim()
            val lastName = apellidosEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val dateBirth = fechaNacEditText.text.toString().trim()
            val userType = tipoUsuarioSpinner.selectedItem.toString()

            if (validateForm(username, password, firstName, lastName, email, dateBirth)) {
                checkForDuplicates(username, email, password, firstName, lastName, dateBirth, userType)
            }
        }

        volverButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun checkForDuplicates(
        username: String,
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        dateBirth: String,
        userType: String
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {

                val existingUsername = db.gimnasioDao().getUserByUsername(username)
                val existingEmail = db.gimnasioDao().getUserByEmail(email)

                withContext(Dispatchers.Main) {
                    if (existingUsername != null) {
                        showError("El nombre de usuario '$username' ya existe")
                        return@withContext
                    }

                    if (existingEmail != null) {
                        showError("El email '$email' ya está registrado")
                        return@withContext
                    }

                    registerUser(username, password, firstName, lastName, email, dateBirth, userType)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError("Error al verificar disponibilidad: ${e.message}")
                }
            }
        }
    }

    companion object {
        const val REGIS_USERNAME = "REGIS_USERNAME"
        const val REGIS_PASSWORD = "REGIS_PASSWORD"
    }


    private fun registerUser(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        email: String,
        dateBirth: String,
        userType: String
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val newUser = GimnasioEntity(
                    username = username,
                    password = password,
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    dateBirth = dateBirth,
                    rememberMe = when (userType) {
                        "Trainer" -> "admin"
                        "User" -> "user"
                        else -> "user"
                    }
                )

                db.gimnasioDao().insertUser(newUser)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Usuario registrado exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    clearForm()

                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)

                   intent.putExtra(REGIS_USERNAME, username)
                   intent.putExtra(REGIS_PASSWORD, password)

                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError("Error al registrar usuario: ${e.message}")
                }
            }
        }
    }

    private fun validateForm(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        email: String,
        dateBirth: String
    ): Boolean {
        return when {
            username.isEmpty() -> {
                showError("Por favor ingrese un nombre de usuario")
                false
            }
            username.length < 3 -> {
                showError("El usuario debe tener al menos 3 caracteres")
                false
            }
            password.isEmpty() -> {
                showError("Por favor ingrese una contraseña")
                false
            }
            password.length < 4 -> {
                showError("La contraseña debe tener al menos 4 caracteres")
                false
            }
            firstName.isEmpty() -> {
                showError("Por favor ingrese su nombre")
                false
            }
            lastName.isEmpty() -> {
                showError("Por favor ingrese sus apellidos")
                false
            }
            email.isEmpty() -> {
                showError("Por favor ingrese un email")
                false
            }
            !isValidEmail(email) -> {
                showError("Por favor ingrese un email válido")
                false
            }
            dateBirth.isEmpty() -> {
                showError("Por favor seleccione su fecha de nacimiento")
                false
            }
            else -> true
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun setupDatePicker(fechaNacEditText: EditText) {
        fechaNacEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = String.format(
                        Locale.getDefault(),
                        "%02d/%02d/%d",
                        selectedDay,
                        selectedMonth + 1,
                        selectedYear
                    )
                    fechaNacEditText.setText(formattedDate)
                },
                year, month, day
            )
            datePicker.show()
        }
    }

    private fun clearForm() {
        findViewById<EditText>(R.id.edit_text_reg_login).text.clear()
        findViewById<EditText>(R.id.edit_text_reg_pass).text.clear()
        findViewById<EditText>(R.id.edit_text_reg_nombre).text.clear()
        findViewById<EditText>(R.id.edit_text_reg_apellidos).text.clear()
        findViewById<EditText>(R.id.edit_text_reg_email).text.clear()
        findViewById<EditText>(R.id.edit_text_reg_fecha_nac).text.clear()
        findViewById<Spinner>(R.id.spinner_tipo_usuario).setSelection(0)
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}