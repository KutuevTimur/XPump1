package com.timur.xpump

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.timur.xpump.data.repository.WorkoutRepository
import com.timur.xpump.ui.history.WorkoutHistoryViewModel

// Эта фабрика берет репозиторий из XPumpApp и передает его в ViewModel
class ViewModelFactory(private val repository: WorkoutRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkoutHistoryViewModel(repository) as T
        }
        // Позже мы добавим сюда ProfileViewModel и WorkoutDetailsViewModel
        throw IllegalArgumentException("Неизвестный класс ViewModel")
    }
}