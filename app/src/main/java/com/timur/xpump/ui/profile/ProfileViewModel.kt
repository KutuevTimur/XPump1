package com.timur.xpump.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timur.xpump.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: WorkoutRepository) : ViewModel() {

    data class ProfileState(
        val level: Int = 1,
        val totalXp: Int = 0,
        val workoutCount: Int = 0
    )

    // Слушаем настоящую базу: как только там появилась тренировка, уровень растет!
    val uiState: StateFlow<ProfileState> = repository.allWorkouts.map { workouts ->
        val count = workouts.size
        val xp = count * 50 // 50 опыта за 1 тренировку
        val lvl = (xp / 100) + 1
        ProfileState(
            level = lvl,
            totalXp = xp % 100,
            workoutCount = count
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileState()
    )

    fun createNewWorkout(name: String, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.createEmptyWorkout(name)
            onCreated(id)
        }
    }
}