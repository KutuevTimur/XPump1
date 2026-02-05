package com.timur.xpump.data

import com.timur.xpump.model.Workout

object WorkoutStorage {

    private val workouts = mutableListOf<Workout>() // храним все тренировки в списке

    // Создание новой тренировки
    fun createWorkout(name: String): Workout {
        val id = System.currentTimeMillis() // используем текущий timestamp как уникальный id
        val workout = Workout(id = id, name = name)
        workouts.add(workout) // добавляем тренировку в список
        return workout
    }

    // Получение всех тренировок
    fun getAllWorkouts(): List<Workout> {
        return workouts
    }

    // Получение тренировки по id
    fun getWorkout(id: Long): Workout? {
        return workouts.find { it.id == id }
    }
}