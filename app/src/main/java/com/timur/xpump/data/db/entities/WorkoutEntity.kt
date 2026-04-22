package com.timur.xpump.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val date: Long = System.currentTimeMillis(),
    val duration: Long = 0,
    val isActive: Boolean = false,
    val startTime: Long = 0
)
