package com.timur.xpump.model

data class WorkoutSummary(
    val workoutName: String,
    val totalDurationSeconds: Long,
    val totalSets: Int,
    val averageRestSeconds: Int,
    val totalWeightLifted: Double, // Тоннаж (кг)
    val totalDistance: Double,     // Дистанция (км)
    val totalCardioTime: Int       // Время кардио (сек)
)