package com.esteban.gimnasio

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.esteban.gimnasio.data.entities.Workout
import com.esteban.gimnasio.data.dao.WorkoutDao
import com.esteban.gimnasio.data.MyRoomDatabase

class TrainerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_WORKOUT_ID = "workout_id"
    }

    private lateinit var nameEditText: EditText
    private lateinit var levelEditText: EditText
    private lateinit var numExercisesEditText: EditText
    private lateinit var videoLinkEditText: EditText

    private lateinit var addButton: Button
    private lateinit var modifyButton: Button
    private lateinit var deleteButton: Button

    private lateinit var workoutDao: WorkoutDao

    private var currentWorkoutId: Int = 0
    private var workoutToModify: Workout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
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
        val backButton: Button = findViewById(R.id.button_back)

        currentWorkoutId = intent.getIntExtra(EXTRA_WORKOUT_ID, 0)

        if (currentWorkoutId != 0) {
            setupEditMode()
        } else {
            setupAddMode()
        }

        addButton.setOnClickListener { saveWorkoutData() }
        modifyButton.setOnClickListener { updateWorkoutData() }
        deleteButton.setOnClickListener { deleteWorkoutData() }

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
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
        if (validateInput()) {
            val numExercises = numExercisesEditText.text.toString().toInt()
            val newWorkout = Workout(
                workoutName = nameEditText.text.toString().trim(),
                level = levelEditText.text.toString().trim(),
                numExercises = numExercises,
                videoUrl = videoLinkEditText.text.toString().trim()
            )

            lifecycleScope.launch {
                workoutDao.insert(newWorkout)
                Toast.makeText(this@TrainerActivity, "Rutina añadida correctamente.", Toast.LENGTH_SHORT).show()
                navigateToWorkoutsActivity()
            }
        }
    }

    private fun updateWorkoutData() {
        if (validateInput() && workoutToModify != null) {
            val numExercises = numExercisesEditText.text.toString().toInt()

            val updatedWorkout = workoutToModify!!.copy(
                workoutName = nameEditText.text.toString().trim(),
                level = levelEditText.text.toString().trim(),
                numExercises = numExercises,
                videoUrl = videoLinkEditText.text.toString().trim()
            )

            lifecycleScope.launch {
                workoutDao.update(updatedWorkout)
                Toast.makeText(this@TrainerActivity, "Rutina modificada correctamente.", Toast.LENGTH_SHORT).show()
                navigateToWorkoutsActivity()
            }
        }
    }

    private fun deleteWorkoutData() {
        if (workoutToModify != null) {
            lifecycleScope.launch {
                workoutDao.delete(workoutToModify!!)
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
        val intent = Intent(this, WorkoutsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}