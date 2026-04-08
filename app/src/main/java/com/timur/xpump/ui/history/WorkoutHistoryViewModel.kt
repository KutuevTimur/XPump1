package com.timur.xpump.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timur.xpump.data.repository.WorkoutRepository
import com.timur.xpump.model.Workout
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutHistoryViewModel(private val repository: WorkoutRepository) : ViewModel() {

    // Читаем тренировки из базы.
    // map - превращает WorkoutEntity (формат базы) в Workout (твою модель данных)
    val workouts: StateFlow<List<Workout>> = repository.allWorkouts.map { entities ->
        entities.map { Workout(id = it.id, name = it.name) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // Экономит батарею, когда экран скрыт
        initialValue = emptyList()
    )

    // Временный метод, чтобы мы могли протестить добавление в базу по кнопке
    fun addRandomWorkout() {
        viewModelScope.launch {
            repository.saveWorkout("Тренировка ${System.currentTimeMillis()}", emptyList())
        }
    }
}