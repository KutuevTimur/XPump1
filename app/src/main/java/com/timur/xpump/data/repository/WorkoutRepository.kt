package com.timur.xpump.data.repository

import com.timur.xpump.data.db.dao.WorkoutDao
import com.timur.xpump.data.db.entities.WorkoutEntity
import com.timur.xpump.data.db.entities.WorkoutSetEntity
import com.timur.xpump.data.db.entities.WorkoutWithSets // Проверь, чтобы этот импорт был правильным!
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val workoutDao: WorkoutDao) {

    // ЭТОЙ СТРОЧКИ У ТЕБЯ НЕ ХВАТАЛО:
    val allWorkoutsWithSets: Flow<List<WorkoutWithSets>> = workoutDao.getAllWorkoutsWithSets()

    val allWorkouts: Flow<List<WorkoutEntity>> = workoutDao.getAllWorkouts()

    suspend fun saveWorkout(name: String, sets: List<com.timur.xpump.model.WorkoutSet>) {
        val workoutId = workoutDao.insertWorkout(WorkoutEntity(name = name))
        val setEntities = sets.map {
            WorkoutSetEntity(workoutId = workoutId, weight = it.weight, reps = it.reps)
        }
        workoutDao.insertSets(setEntities)
    }

    suspend fun createEmptyWorkout(name: String): Long {
        return workoutDao.insertWorkout(WorkoutEntity(name = name))
    }

    suspend fun addSet(workoutId: Long, weight: Int, reps: Int) {
        workoutDao.insertSet(WorkoutSetEntity(workoutId = workoutId, weight = weight, reps = reps))
    }

    suspend fun removeLastSet(workoutId: Long) {
        workoutDao.deleteLastSet(workoutId)
    }

    fun getWorkoutFlow(id: Long): Flow<WorkoutWithSets?> {
        return workoutDao.getWorkoutWithSetsFlow(id)
    }

    // New methods to resolve the error
    suspend fun insertWorkout(workout: WorkoutEntity): Long {
        return workoutDao.insertWorkout(workout)
    }

    suspend fun insertWorkoutSet(workoutSet: WorkoutSetEntity) {
        workoutDao.insertSet(workoutSet)
    }
}
