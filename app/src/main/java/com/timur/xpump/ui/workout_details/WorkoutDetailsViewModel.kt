package com.timur.xpump.ui.workout_details

import androidx.lifecycle.ViewModel
import com.timur.xpump.data.WorkoutStorage
import com.timur.xpump.model.WorkoutSet

class WorkoutDetailsViewModel : ViewModel() {

    private var workoutId: Long = -1L

    fun init(workoutId: Long) {
        if (this.workoutId == -1L) {
            this.workoutId = workoutId
        }
    }

    fun getWorkoutName(): String {
        return WorkoutStorage.getWorkout(workoutId)?.name ?: "Тренировка"
    }

    fun getSets(): List<WorkoutSet> {
        return WorkoutStorage.getWorkout(workoutId)?.sets ?: emptyList()
    }

    fun getSetsCount(): Int {
        return getSets().size
    }

    fun addSet(weightText: String, repsText: String): Boolean {
        val weight = weightText.toIntOrNull()
        val reps = repsText.toIntOrNull()

        if (weight == null || reps == null) return false
        if (weight <= 0 || reps <= 0) return false

        val workout = WorkoutStorage.getWorkout(workoutId) ?: return false
        workout.sets.add(WorkoutSet(weight, reps))
        return true
    }

    fun removeSet() {
        val workout = WorkoutStorage.getWorkout(workoutId) ?: return
        if (workout.sets.isNotEmpty()) {
            workout.sets.removeAt(workout.sets.lastIndex)
        }
    }
}
