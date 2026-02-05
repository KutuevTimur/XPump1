package com.timur.xpump.ui.history

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WorkoutHistoryViewModel : ViewModel() {

    private val _workouts = MutableStateFlow<List<String>>(emptyList())
    val workouts: StateFlow<List<String>> = _workouts

    fun addWorkout(name: String) {
        _workouts.value = _workouts.value + name
    }
}
