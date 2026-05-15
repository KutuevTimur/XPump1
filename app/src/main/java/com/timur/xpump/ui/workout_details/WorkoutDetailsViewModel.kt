package com.timur.xpump.ui.workout_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timur.xpump.data.db.entities.WorkoutWithSets
import com.timur.xpump.data.repository.WorkoutRepository
import com.timur.xpump.utils.ExerciseUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WorkoutDetailsViewModel(private val repository: WorkoutRepository) : ViewModel() {

    private var workoutId: Long = -1L

    // Храним время отдыха в секундах (по умолчанию 90)
    private var _defaultRestTimeSeconds = 90
    val defaultRestTimeSeconds: Int get() = _defaultRestTimeSeconds

    // Реактивное состояние экрана
    private val _workoutData = MutableStateFlow<WorkoutWithSets?>(null)
    val workoutData: StateFlow<WorkoutWithSets?> = _workoutData

    fun init(id: Long) {
        if (workoutId == -1L) {
            workoutId = id
            // Начинаем слушать базу данных
            viewModelScope.launch {
                repository.getWorkoutFlow(id).collect { data ->
                    _workoutData.value = data
                }
            }
        }
    }

    fun updateDefaultRestTime(timeStr: String) {
        val seconds = ExerciseUtils.parseMMSSToSeconds(timeStr)
        if (seconds > 0) {
            _defaultRestTimeSeconds = seconds
        }
    }

    fun addSet(weightText: String, repsText: String, exerciseName: String, setType: String = "NORMAL", timeSeconds: Int = 0, distance: Double = 0.0): Boolean {
        val weight = weightText.toIntOrNull() ?: 0
        val reps = repsText.toIntOrNull() ?: 0
        if (exerciseName.isEmpty()) return false

        viewModelScope.launch {
            repository.addSet(workoutId, weight, reps, exerciseName, setType, timeSeconds, distance)
        }
        return true
    }

    fun removeSet() {
        viewModelScope.launch {
            repository.removeLastSet(workoutId)
        }
    }

    fun deleteSpecificSet(setId: Long) {
        viewModelScope.launch {
            repository.deleteSpecificSet(setId)
        }
    }

    fun finishWorkout(durationSeconds: Long) {
        viewModelScope.launch {
            repository.finishWorkout(workoutId, durationSeconds)
        }
    }
}
