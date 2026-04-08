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
import java.util.Calendar // Import Calendar for date generation

class WorkoutHistoryViewModel(private val repository: WorkoutRepository) : ViewModel() {

    // Читаем тренировки из базы.
    // map - превращает WorkoutEntity (формат базы) в Workout (твою модель данных)
    val workouts: StateFlow<List<Workout>> = repository.allWorkoutsWithSets.map { list ->
        list.map { item ->
            Workout(
                id = item.workout.id,
                name = item.workout.name,
                // Мапим подходы из БД в модели для UI
                sets = item.sets.map {
                    com.timur.xpump.model.WorkoutSet(it.weight, it.reps)
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

        // Добавляем несколько случайных подходов для новой тренировки
        for (i in 1..3) {
            val weight = (50..100).random().toDouble()
            val reps = (8..12).random()
            repository.insertWorkoutSet(WorkoutSetEntity(workoutId = workoutId, weight = weight, reps = reps))
        }
    }
}
