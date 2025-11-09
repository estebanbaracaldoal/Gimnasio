package com.esteban.gimnasio.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
//import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.esteban.gimnasio.R
import com.esteban.gimnasio.data.entities.Workout

class WorkoutAdapter(
    private var workouts: List<Workout>,
    private val onVideoClick: (String) -> Unit,
    private val onEditClick: (Int) -> Unit
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {
    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tv_workout_name)
        val levelTextView: TextView = itemView.findViewById(R.id.tv_level_value)
        val numExercisesTextView: TextView = itemView.findViewById(R.id.tv_exercises_value)
        val videoButton: ImageButton = itemView.findViewById(R.id.btn_view_video)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val currentWorkout = workouts[position]

        holder.nameTextView.text = currentWorkout.workoutName
        holder.levelTextView.text = currentWorkout.level
        holder.numExercisesTextView.text = currentWorkout.numExercises.toString()

        holder.videoButton.setOnClickListener {
            onVideoClick(currentWorkout.videoUrl)
        }

        holder.itemView.setOnClickListener {
            onEditClick(currentWorkout.id)
        }
    }
    override fun getItemCount(): Int = workouts.size
    fun updateList(newWorkouts: List<Workout>) {
        workouts = newWorkouts
        notifyDataSetChanged()

    }
}