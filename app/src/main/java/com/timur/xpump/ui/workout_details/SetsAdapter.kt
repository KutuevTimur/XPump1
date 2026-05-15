package com.timur.xpump.ui.workout_details

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.timur.xpump.databinding.ItemSetBinding
import com.timur.xpump.model.WorkoutSet
import com.timur.xpump.utils.ExerciseCategory
import com.timur.xpump.utils.ExerciseUtils

class SetsAdapter : RecyclerView.Adapter<SetsAdapter.SetViewHolder>() {

    private val items = mutableListOf<WorkoutSet>()

    // Обновляем список объектов WorkoutSet
    fun submitList(newList: List<WorkoutSet>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {
        val binding = ItemSetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    class SetViewHolder(private val binding: ItemSetBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(set: WorkoutSet, position: Int) {
            binding.tvSetNumber.text = (position + 1).toString()
            binding.tvExerciseName.text = set.exerciseName

            val category = ExerciseUtils.getCategoryByName(set.exerciseName)
            val infoText = when (category) {
                ExerciseCategory.CARDIO -> {
                    val timeStr = ExerciseUtils.formatSecondsToMMSS(set.timeSeconds)
                    "Дист: ${set.distance} км • Время: $timeStr"
                }
                ExerciseCategory.STATIC -> {
                    val timeStr = ExerciseUtils.formatSecondsToMMSS(set.timeSeconds)
                    "Время: $timeStr"
                }
                ExerciseCategory.STRENGTH -> {
                    "${set.weight} кг x ${set.reps}"
                }
            }
            binding.tvSetMetrics.text = infoText

            // 2. Отображение тегов подходов (Бейджи)
            when (set.setType) {
                "WARMUP" -> {
                    binding.tvSetTypeBadge.text = "РАЗМИНКА"
                    binding.tvSetTypeBadge.visibility = View.VISIBLE
                    binding.tvSetTypeBadge.setTextColor(Color.parseColor("#F57F17"))
                }
                "DROPSET" -> {
                    binding.tvSetTypeBadge.text = "ДРОПСЕТ"
                    binding.tvSetTypeBadge.visibility = View.VISIBLE
                    binding.tvSetTypeBadge.setTextColor(Color.parseColor("#6A1B9A"))
                }
                "FAILURE" -> {
                    binding.tvSetTypeBadge.text = "ОТКАЗ"
                    binding.tvSetTypeBadge.visibility = View.VISIBLE
                    binding.tvSetTypeBadge.setTextColor(Color.parseColor("#D32F2F"))
                }
                else -> {
                    binding.tvSetTypeBadge.visibility = View.GONE
                }
            }
        }
    }
}
