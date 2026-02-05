package com.timur.xpump.model

data class Workout(
    val id: Long,
    val name: String,
    val sets: MutableList<WorkoutSet> = mutableListOf()
)
