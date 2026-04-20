package com.timur.xpump.model

data class WorkoutSet(
    val weight: Int,
    val reps: Int,
    val exerciseName: String = "Упражнение" // Добавили имя для красоты в списке
)
