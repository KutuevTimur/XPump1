package com.timur.xpump.ui.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {

    private val _workoutCount = MutableStateFlow(0)
    val workoutCount: StateFlow<Int> = _workoutCount

    fun increment() {
        _workoutCount.value++
    }
}
