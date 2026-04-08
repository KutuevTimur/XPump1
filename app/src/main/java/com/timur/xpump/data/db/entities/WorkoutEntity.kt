package com.timur.xpump.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts") // [cite: 53]
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // Пусть база сама ставит ID [cite: 252]
    val name: String,
    val date: Long = System.currentTimeMillis() // Добавляем дату, как в плане [cite: 252, 253]
)