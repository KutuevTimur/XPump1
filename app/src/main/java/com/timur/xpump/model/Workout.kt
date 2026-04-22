package com.timur.xpump.model

data class Workout(
    val id: Long = 0,
    val name: String,
    val dateFormatted: String = "",
    val duration: Long = 0, // Это поле ОБЯЗАТЕЛЬНО должно быть тут
    val sets: MutableList<WorkoutSet> = mutableListOf()
)
