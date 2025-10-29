package com.esteban.gimnasio

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // Asegúrate de que el nombre del layout sea correcto

        // 1. Conectar los elementos de la interfaz (XML) con el código (Kotlin)
        val usernameEditText: EditText = findViewById(R.id.edit_text_username)
        val passwordEditText: EditText = findViewById(R.id.edit_text_password)
        val loginButton: Button = findViewById(R.id.button_login)
        val rememberMeCheckbox: CheckBox = findViewById(R.id.checkbox_remember)
        val createAccountTextView: TextView = findViewById(R.id.text_create_account)

        // ------ //

        // Apuntes de ROOM - hay un if para precargar la BBDD

        // ------ //

        // ------ //

        // Si el check esta marcado, coger el unico usuario de la tabla remmber me y ponerlo en los campos

        // ------ //

        // 2. Manejar el evento de clic del botón "Iniciar Sesión"
        loginButton.setOnClickListener {
            // 3. Obtener el texto de los campos
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val rememberMe = rememberMeCheckbox.isChecked

            // ** LÓGICA DE VALIDACIÓN (Ejemplo simple) **
            if (username.isEmpty() || password.isEmpty()) {
                // Si algún campo está vacío, mostramos una alerta
                Toast.makeText(this, "Por favor, ingresa usuario y contraseña.", Toast.LENGTH_SHORT).show()
            } else {

                // ------ //

                // poner un if para comprobar el user/pass
                // Apuntes de ROOM - Como se hace una select

                // ------ //


                // Si ambos campos están llenos, mostramos los datos (en una app real aquí harías la llamada a tu servidor)
                val mensaje = "Iniciando sesión con: $username. Recuérdame: $rememberMe"
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()

                // Aquí iría el código para navegar a la pantalla principal (MainActivity)
                val intent = Intent(this, WorkoutsActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // 4. Manejar el evento de clic del texto "Crea una aquí"
        createAccountTextView.setOnClickListener {
            //Toast.makeText(this, "¡Navegando a la pantalla de registro!", Toast.LENGTH_SHORT).show()
            // Aquí iría el código para navegar a la pantalla de registro (RegisterActivity)

            // val intent = Intent(this, RegisterActivity::class.java)
            val intent = Intent(this, RegisterActivity::class.java)
            // startActivity(intent)
            startActivity(intent)
        }

        // ------ //

        // Evento del check para ok/nook

        // ------ //
    }
}