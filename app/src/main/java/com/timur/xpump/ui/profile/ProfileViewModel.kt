package com.timur.xpump.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timur.xpump.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: WorkoutRepository) : ViewModel() {

    data class ProfileState(
        val level: Int = 1,
        val totalXp: Int = 0,
        val workoutCount: Int = 0,
        val benchPressRank: String = "-",
        val squatRank: String = "-"
    )

    private val maxBenchPressWeight = repository.getMaxWeightForExercise("%Жим%")
    private val maxSquatWeight = repository.getMaxWeightForExercise("%Присед%")

    // Слушаем настоящую базу: как только там появилась тренировка, уровень растет!
    val uiState: StateFlow<ProfileState> = combine(
        repository.allWorkouts,
        maxBenchPressWeight,
        maxSquatWeight
    ) { workouts, maxBenchPress, maxSquat ->
        val count = workouts.size
        val xp = count * 50 // 50 опыта за 1 тренировку
        val lvl = (xp / 100) + 1

        val benchPressRank = calculateRank(maxBenchPress)
        val squatRank = calculateRank(maxSquat)

        ProfileState(
            level = lvl,
            totalXp = xp % 100,
            workoutCount = count,
            benchPressRank = benchPressRank,
            squatRank = squatRank
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileState()
    )

    private fun calculateRank(weight: Int?): String {
        return when (weight) {
            null -> "Нет данных"
            in 0..49 -> "Новичок"
            in 50..80 -> "3 разряд"
            in 81..100 -> "2 разряд"
            else -> "1 разряд"
        }
    }

    fun createNewWorkout(name: String, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.createEmptyWorkout(name)
            onCreated(id)
        }
    }
}