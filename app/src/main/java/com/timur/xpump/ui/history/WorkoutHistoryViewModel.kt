package com.timur.xpump.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timur.xpump.data.repository.WorkoutRepository
import com.timur.xpump.model.Workout
import com.timur.xpump.model.WorkoutSet
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WorkoutHistoryViewModel(private val repository: WorkoutRepository) : ViewModel() {

    // Форматер: переводит системное время в "20 апр 2026, 15:30"
    // Теперь будет: "понедельник, 20 апреля, 14:30"
    private val formatter = SimpleDateFormat("EEEE, d MMMM, HH:mm", Locale("ru"))

    val workouts: StateFlow<List<Workout>> = repository.allWorkoutsWithSets.map { list ->
        list.map { item ->
            Workout(
                id = item.workout.id,
                name = item.workout.name,
                dateFormatted = formatter.format(Date(item.workout.date)),
                duration = item.workout.duration, // ДОБАВЬ ЭТУ СТРОЧКУ!
                sets = item.sets.map {
                    WorkoutSet(id = it.id, weight = it.weight.toInt(), reps = it.reps.toInt(), exerciseName = it.exerciseName ?: "Упражнение")
                }.toMutableList()
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Нормальное создание новой тренировки (как в Профиле)
    fun createNewWorkout(onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.createEmptyWorkout("Новая тренировка")
            onCreated(id)
        }
    }

    fun deleteWorkout(workoutId: Long) {
        viewModelScope.launch {
            repository.deleteWorkout(workoutId)
        }
    }
}
