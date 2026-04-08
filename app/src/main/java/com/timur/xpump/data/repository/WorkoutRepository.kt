package com.timur.xpump.data.repository

import com.timur.xpump.data.db.dao.WorkoutDao
import com.timur.xpump.data.db.entities.WorkoutEntity
import com.timur.xpump.data.db.entities.WorkoutSetEntity
import com.timur.xpump.data.db.entities.WorkoutWithSets
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val workoutDao: WorkoutDao) {

    // Получаем все тренировки (авто-обновление через Flow) [cite: 130-131]
    val allWorkouts: Flow<List<WorkoutEntity>> = workoutDao.getAllWorkouts()

    // Сохранение тренировки и её подходов в одной транзакции
    suspend fun saveWorkout(name: String, sets: List<com.timur.xpump.model.WorkoutSet>) {
        // 1. Сохраняем саму тренировку и получаем её ID [cite: 239, 281]
        val workoutId = workoutDao.insertWorkout(WorkoutEntity(name = name))

        // 2. Превращаем доменные модели подходов в Entity для БД
        val setEntities = sets.map {
            WorkoutSetEntity(workoutId = workoutId, weight = it.weight, reps = it.reps)
        }

        // 3. Сохраняем все подходы [cite: 239, 282]
        workoutDao.insertSets(setEntities)
    }

    suspend fun getFullWorkout(id: Long): WorkoutWithSets? {
        return workoutDao.getWorkoutWithSets(id)
    }
}