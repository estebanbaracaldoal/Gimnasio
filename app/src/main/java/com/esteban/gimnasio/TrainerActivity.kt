package com.esteban.gimnasio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.esteban.gimnasio.data.MyRoomDatabase
import com.esteban.gimnasio.data.dao.WorkoutDao
import com.esteban.gimnasio.data.entities.Workout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class TrainerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_WORKOUT_ID = "extra_workout_id"
        private const val PREFS_FILE = "profilePrefs"
    }

    private lateinit var nameEditText: EditText
    private lateinit var levelEditText: EditText
    private lateinit var numExercisesEditText: EditText
    private lateinit var videoLinkEditText: EditText
    private lateinit var addButton: Button
    private lateinit var modifyButton: Button
    private lateinit var deleteButton: Button
    private lateinit var backButton: Button

    private lateinit var workoutDao: WorkoutDao
    private var workoutToModify: Workout? = null
    private var currentWorkoutId: Int = 0

    private lateinit var preferences: android.content.SharedPreferences

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences(PREFS_FILE, MODE_PRIVATE)
        val language = prefs.getString("app_language", "Español") ?: "Español"
        super.attachBaseContext(updateLocaleContext(newBase, language))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        preferences = getSharedPreferences(PREFS_FILE, MODE_PRIVATE)

        // Aplica tema
        val isDarkMode = preferences.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trainer)


        val db = MyRoomDatabase.getDatabase(applicationContext)
        workoutDao = db.workoutDao()


        nameEditText = findViewById(R.id.edit_text_trainer_name)
        levelEditText = findViewById(R.id.edit_text_trainer_level)
        numExercisesEditText = findViewById(R.id.edit_text_num_exercises)
        videoLinkEditText = findViewById(R.id.edit_text_workout_video)
        addButton = findViewById(R.id.button_add_trainer_data)
        modifyButton = findViewById(R.id.button_modify_trainer_data)
        deleteButton = findViewById(R.id.button_delete_trainer_data)
        backButton = findViewById(R.id.button_back)

        currentWorkoutId = intent.getIntExtra(EXTRA_WORKOUT_ID, 0)

        if (currentWorkoutId != 0) {
            setupEditMode()
        } else {
            setupAddMode()
        }


        addButton.setOnClickListener { saveWorkoutData() }
        modifyButton.setOnClickListener { updateWorkoutData() }
        deleteButton.setOnClickListener { deleteWorkoutData() }
        backButton.setOnClickListener { onBackPressed() }
    }

    private fun setupAddMode() {
        addButton.visibility = Button.VISIBLE
        modifyButton.visibility = Button.GONE
        deleteButton.visibility = Button.GONE
    }

    private fun setupEditMode() {
        addButton.visibility = Button.GONE
        modifyButton.visibility = Button.VISIBLE
        deleteButton.visibility = Button.VISIBLE

        lifecycleScope.launch {
            workoutToModify = workoutDao.getWorkoutById(currentWorkoutId)

            workoutToModify?.let { workout ->
                nameEditText.setText(workout.workoutName)
                levelEditText.setText(workout.level)
                numExercisesEditText.setText(workout.numExercises.toString())
                videoLinkEditText.setText(workout.videoUrl)
            } ?: run {
                Toast.makeText(this@TrainerActivity, "Error: Rutina no encontrada.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun saveWorkoutData() {
        if (!validateInput()) return

        val numExercises = numExercisesEditText.text.toString().toInt()
        val newWorkout = Workout(
            workoutName = nameEditText.text.toString().trim(),
            level = levelEditText.text.toString().trim(),
            numExercises = numExercises,
            videoUrl = videoLinkEditText.text.toString().trim()
        )

        lifecycleScope.launch(Dispatchers.IO) {
            workoutDao.insert(newWorkout)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@TrainerActivity, "Rutina añadida correctamente.", Toast.LENGTH_SHORT).show()
                navigateToWorkoutsActivity()
            }
        }
    }
    private fun updateWorkoutData() {
        if (!validateInput() || workoutToModify == null) return

        val updatedWorkout = workoutToModify!!.copy(
            workoutName = nameEditText.text.toString().trim(),
            level = levelEditText.text.toString().trim(),
            numExercises = numExercisesEditText.text.toString().toInt(),
            videoUrl = videoLinkEditText.text.toString().trim()
        )

        lifecycleScope.launch(Dispatchers.IO) {
            workoutDao.update(updatedWorkout)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@TrainerActivity, "Rutina modificada correctamente.", Toast.LENGTH_SHORT).show()
                navigateToWorkoutsActivity()
            }
        }
    }
    private fun deleteWorkoutData() {
        if (workoutToModify == null) return

        lifecycleScope.launch(Dispatchers.IO) {
            workoutDao.delete(workoutToModify!!)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@TrainerActivity, "Rutina eliminada correctamente.", Toast.LENGTH_SHORT).show()
                navigateToWorkoutsActivity()
            }
        }
    }
    private fun validateInput(): Boolean {
        val name = nameEditText.text.toString().trim()
        val level = levelEditText.text.toString().trim()
        val numExercisesText = numExercisesEditText.text.toString().trim()
        val videoLink = videoLinkEditText.text.toString().trim()

        if (name.isBlank() || level.isBlank() || numExercisesText.isBlank() || videoLink.isBlank()) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (numExercisesText.toIntOrNull() == null || numExercisesText.toInt() <= 0) {
            numExercisesEditText.error = "Número de ejercicios no válido."
            return false
        }

        return true
    }
    private fun navigateToWorkoutsActivity() {
        val sharedPref = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val username = sharedPref.getString("active_username", null)

        if (username != null) {
            lifecycleScope.launch {
                val db = MyRoomDatabase.getDatabase(applicationContext)
                val user = db.gimnasioDao().getUserByUsername(username)

                withContext(Dispatchers.Main) {
                    val intent = Intent(this@TrainerActivity, WorkoutsActivity::class.java)
                    if (user != null) {
                        intent.putExtra(WorkoutsActivity.USER_ROLE_KEY, user.rememberMe.lowercase(Locale.ROOT))
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        } else {
            val intent = Intent(this@TrainerActivity, WorkoutsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun updateLocaleContext(base: Context, language: String): Context {
        val localeCode = when (language.lowercase(Locale.ROOT)) {
            "spanish" -> "es"
            else -> "en"
        }
        val locale = Locale.forLanguageTag(localeCode)
        val config = base.resources.configuration
        config.setLocales(android.os.LocaleList(locale))
        return base.createConfigurationContext(config)
    }
}
