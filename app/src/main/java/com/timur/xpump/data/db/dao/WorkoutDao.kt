package com.timur.xpump.data.db.dao

import androidx.room.*
import com.timur.xpump.data.db.entities.WorkoutEntity
import com.timur.xpump.data.db.entities.WorkoutSetEntity
import com.timur.xpump.data.db.entities.WorkoutWithSets
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    // Получение всех тренировок для списка истории (самые свежие сверху) [cite: 239]
    @Query("SELECT * FROM workouts ORDER BY date DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    // Вставка тренировки (возвращает сгенерированный ID) [cite: 239]
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    // Вставка списка подходов для конкретной тренировки [cite: 239]
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<WorkoutSetEntity>)

    // Получение конкретной тренировки со всеми её подходами [cite: 239]
    // @Transaction обязательна, так как Room делает два запроса к разным таблицам
    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutWithSets(id: Long): WorkoutWithSets?

    // Удаление тренировки (если нужно)
    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)

    // Удаление всех подходов конкретной тренировки (например, при очистке)
    @Query("DELETE FROM workout_sets WHERE workoutId = :workoutId")
    suspend fun deleteSetsByWorkoutId(workoutId: Long)
}