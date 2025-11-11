package com.esteban.gimnasio

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.esteban.gimnasio.data.MyRoomDatabase
import com.esteban.gimnasio.data.adapter.WorkoutAdapter
import com.esteban.gimnasio.data.dao.WorkoutDao

class WorkoutsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WorkoutAdapter
    private lateinit var workoutDao: WorkoutDao
    private lateinit var filterEditText: EditText
    private lateinit var filterButton: Button
    private lateinit var centerActionButton: Button

    companion object {
        const val USER_ROLE_KEY = "USER_ROLE"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workouts)

        val db = MyRoomDatabase.getDatabase(applicationContext)
        workoutDao = db.workoutDao()

        val userRole = intent.getStringExtra(USER_ROLE_KEY) ?: "user"
        val adminPrivileges = userRole == "admin"


        val backButton: Button = findViewById(R.id.btn_back)
        val profileButton: Button = findViewById(R.id.btn_profile)
        centerActionButton = findViewById(R.id.btn_center_action)

        recyclerView = findViewById(R.id.rv_workouts_list)
        filterEditText = findViewById(R.id.et_filter_level)
        filterButton = findViewById(R.id.btn_filter)

        if (adminPrivileges) {
            centerActionButton.text = getString(R.string.button_add_new_workout)
            centerActionButton.visibility = View.VISIBLE
            centerActionButton.setOnClickListener {
                val intent = Intent(this, TrainerActivity::class.java)
                startActivity(intent)
            }
        } else {
            centerActionButton.visibility = View.GONE
            centerActionButton.setOnClickListener(null)
        }

        adapter = WorkoutAdapter(
            workouts = emptyList(),
            onVideoClick = { videoLink -> openWorkoutVideo(videoLink) },
            onEditClick = { workoutId ->
                if (adminPrivileges) {
                    val intent = Intent(this, TrainerActivity::class.java).apply {
                        putExtra(TrainerActivity.EXTRA_WORKOUT_ID, workoutId)
                    }
                    startActivity(intent)
                }
            })
        recyclerView.adapter = adapter

        observeTrainersData()

        filterButton.setOnClickListener {
            val level = filterEditText.text.toString().trim()
            if (level.isNotBlank()) {
                observeFilteredTrainers(level)
            } else {
                observeTrainersData()
            }
        }

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeTrainersData() {
        workoutDao.getAllWorkouts().observe(this) { workouts ->
            adapter.updateList(workouts)
        }
    }

    private fun observeFilteredTrainers(level: String) {
        workoutDao.getWorkoutsByLevel(level).observe(this) { workouts ->
            adapter.updateList(workouts)
            if (workouts.isEmpty()) {
                Toast.makeText(
                    this, "No se encontraron  niveles de workouts: $level", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun openWorkoutVideo(videoLink: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoLink))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Enlink no válido, revise el link.", Toast.LENGTH_LONG).show()
        }
    }
}