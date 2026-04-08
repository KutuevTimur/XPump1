package com.timur.xpump.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "workout_sets") // [cite: 53]
data class WorkoutSetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutId: Long, // Ссылка на тренировку (Foreign Key) [cite: 256]
    val weight: Int,
    val reps: Int
)