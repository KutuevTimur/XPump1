package com.timur.xpump.ui.profile

import androidx.lifecycle.ViewModel
import com.timur.xpump.data.WorkoutStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState

    // Модель состояния экрана для иммутабельности (как в твоем плане) [cite: 147-150]
    data class ProfileState(
        val level: Int = 1,
        val totalXp: Int = 0,
        val workoutCount: Int = 0,
        val xpToNextLevel: Int = 100
    )

    fun refreshProfile() {
        val workouts = WorkoutStorage.getAllWorkouts()
        val count = workouts.size

        // Допустим, 1 тренировка = 50 XP (потом вынесем в UseCase) [cite: 284-285]
        val xp = count * 50
        val lvl = (xp / 100) + 1

        _uiState.value = ProfileState(
            level = lvl,
            totalXp = xp % 100,
            workoutCount = count
        )
    }
}
