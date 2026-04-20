package com.timur.xpump.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timur.xpump.data.db.entities.WorkoutEntity
import com.timur.xpump.data.db.entities.WorkoutSetEntity
import com.timur.xpump.data.repository.WorkoutRepository
import com.timur.xpump.model.Workout
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class WorkoutHistoryViewModel(private val repository: WorkoutRepository) : ViewModel() {

    val workouts: StateFlow<List<Workout>> = repository.allWorkoutsWithSets.map { list ->
        list.map { item ->
            Workout(
                id = item.workout.id,
                name = item.workout.name,
                date = item.workout.date, // Теперь дата прокидывается!
                sets = item.sets.map {
                    com.timur.xpump.model.WorkoutSet(
                        weight = it.weight,
                        reps = it.reps,
                        exerciseName = it.exerciseName ?: "Упражнение"
                    )
                }.toMutableList()
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addRandomWorkout() = viewModelScope.launch {
        val timestamp = Calendar.getInstance().timeInMillis
        val newWorkout = WorkoutEntity(name = "Workout $timestamp", date = timestamp)
        val workoutId = repository.insertWorkout(newWorkout)

        for (i in 1..3) {
            val weight = (50..100).random()
            val reps = (8..12).random()
            val exerciseName = if (i % 2 == 0) "Жим" else "Присед"
            repository.addSet(workoutId, weight, reps, exerciseName)
        }
    }
}
