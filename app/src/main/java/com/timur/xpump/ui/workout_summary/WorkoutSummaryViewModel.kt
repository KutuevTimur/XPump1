package com.timur.xpump.ui.workout_summary

import androidx.lifecycle.ViewModel
import com.timur.xpump.data.db.entities.WorkoutWithSets
import com.timur.xpump.model.WorkoutSummary
import com.timur.xpump.utils.ExerciseCategory
import com.timur.xpump.utils.ExerciseUtils

class WorkoutSummaryViewModel : ViewModel() {

    fun calculateSummary(workoutWithSets: WorkoutWithSets): WorkoutSummary {
        // ИСПРАВЛЕНО: обращаемся к .sets, а не .workoutSets
        val setsList = workoutWithSets.sets
        val duration = workoutWithSets.workout.duration

        var totalWeight = 0.0
        var totalDist = 0.0
        var totalCardioT = 0
        var strengthSetsCount = 0

        setsList.forEach { set ->
            val category = ExerciseUtils.getCategoryByName(set.exerciseName ?: "")
            when (category) {
                ExerciseCategory.STRENGTH -> {
                    totalWeight += (set.weight * set.reps)
                    strengthSetsCount++
                }
                ExerciseCategory.CARDIO -> {
                    totalDist += set.distance
                    totalCardioT += set.timeSeconds
                }
                ExerciseCategory.STATIC -> {
                    totalCardioT += set.timeSeconds
                }
            }
        }

        // Средний отдых (пока заглушка)
        val avgRest = if (setsList.size > 1) 90 else 0

        return WorkoutSummary(
            workoutName = workoutWithSets.workout.name,
            totalDurationSeconds = duration,
            totalSets = setsList.size,
            averageRestSeconds = avgRest,
            totalWeightLifted = totalWeight,
            totalDistance = totalDist,
            totalCardioTime = totalCardioT
        )
    }
}