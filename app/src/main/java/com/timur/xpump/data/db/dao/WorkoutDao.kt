package com.timur.xpump.data.db.dao

import androidx.room.*
import com.timur.xpump.data.db.entities.WorkoutEntity
import com.timur.xpump.data.db.entities.WorkoutSetEntity
import com.timur.xpump.data.db.entities.WorkoutWithSets
import kotlinx.coroutines.flow.Flow
import androidx.room.Transaction


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

    // Добавить один подход
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: WorkoutSetEntity)

    // Удалить последний добавленный подход у конкретной тренировки (магия SQLite)
    @Query("DELETE FROM workout_sets WHERE id = (SELECT id FROM workout_sets WHERE workoutId = :workoutId ORDER BY id DESC LIMIT 1)")
    suspend fun deleteLastSet(workoutId: Long)

    // ЖИВОЙ поток данных: если подходы меняются, UI обновится сам
    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :id")
    fun getWorkoutWithSetsFlow(id: Long): Flow<WorkoutWithSets?>

    // Добавь это к остальным запросам в WorkoutDao
    @Transaction
    @Query("SELECT * FROM workouts ORDER BY date DESC")
    fun getAllWorkoutsWithSets(): Flow<List<WorkoutWithSets>> // Именно так!




}