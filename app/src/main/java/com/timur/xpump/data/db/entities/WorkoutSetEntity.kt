package com.timur.xpump.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_sets")
data class WorkoutSetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutId: Long,
    val weight: Int,
    val reps: Int,
    val exerciseName: String? = "Упражнение" // Добавили поле в БД
)