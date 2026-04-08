package com.timur.xpump.data.repository

import com.timur.xpump.data.db.dao.WorkoutDao
import com.timur.xpump.data.db.entities.WorkoutEntity
import com.timur.xpump.data.db.entities.WorkoutSetEntity
import com.timur.xpump.data.db.entities.WorkoutWithSets
import kotlinx.coroutines.flow.Flow
import com.timur.xpump.data.db.dao.WorkoutWithSets

class WorkoutRepository(private val workoutDao: WorkoutDao) {

    // Получаем все тренировки (авто-обновление через Flow) [cite: 130-131]
    val allWorkoutsWithSets: Flow<List<WorkoutWithSets>> = workoutDao.getAllWorkoutsWithSets()

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

    suspend fun createEmptyWorkout(name: String): Long {
        // Создаем тренировку прямо в SQLite!
        return workoutDao.insertWorkout(com.timur.xpump.data.db.entities.WorkoutEntity(name = name))
    }


    // Добавляем подход
    suspend fun addSet(workoutId: Long, weight: Int, reps: Int) {
        workoutDao.insertSet(WorkoutSetEntity(workoutId = workoutId, weight = weight, reps = reps))
    }

    // Удаляем последний подход
    suspend fun removeLastSet(workoutId: Long) {
        workoutDao.deleteLastSet(workoutId)
    }

    // Получаем живой поток тренировки
    fun getWorkoutFlow(id: Long): Flow<WorkoutWithSets?> {
        return workoutDao.getWorkoutWithSetsFlow(id)
    }


    // Это для Истории, чтобы сразу видеть количество подходов
    val allWorkoutsWithSets: Flow<List<WorkoutWithSets>> = workoutDao.getAllWorkoutsWithSets()
}