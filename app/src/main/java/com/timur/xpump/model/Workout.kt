package com.timur.xpump.model

data class Workout(
    val id: Long,
    val name: String,
    val date: Long = 0, // Добавлено поле date
    val sets: MutableList<WorkoutSet> = mutableListOf()
)
