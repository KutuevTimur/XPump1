package com.timur.xpump.model

data class WorkoutSet(
    val id: Long = 0,
    val weight: Int,
    val reps: Int,
    val exerciseName: String,
    val setType: String = "NORMAL",
    val timeSeconds: Int = 0,
    val distance: Double = 0.0
)
