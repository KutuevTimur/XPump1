package com.timur.xpump

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.timur.xpump.data.repository.WorkoutRepository
import com.timur.xpump.ui.history.WorkoutHistoryViewModel
import com.timur.xpump.ui.profile.ProfileViewModel
import com.timur.xpump.ui.workout_details.WorkoutDetailsViewModel

class ViewModelFactory(private val repository: WorkoutRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return WorkoutHistoryViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return ProfileViewModel(repository) as T
        }
        // ДОБАВИЛИ ЭТОТ БЛОК
        if (modelClass.isAssignableFrom(WorkoutDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return WorkoutDetailsViewModel(repository) as T
        }
        throw IllegalArgumentException("Неизвестный класс ViewModel")
    }
}