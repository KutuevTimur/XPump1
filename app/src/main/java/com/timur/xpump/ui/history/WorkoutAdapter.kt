package com.timur.xpump.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.timur.xpump.databinding.ItemWorkoutBinding
import com.timur.xpump.model.Workout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WorkoutAdapter(
    private val onWorkoutClicked: (Long) -> Unit // callback для перехода в детали тренировки
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    private val items = mutableListOf<Workout>()

    val currentList: List<Workout> get() = items

    fun submitList(list: List<Workout>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val binding = ItemWorkoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class WorkoutViewHolder(private val binding: ItemWorkoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(workout: Workout) {
            binding.tvWorkoutName.text = workout.name
            // Считаем минуты
            val mins = workout.duration / 60
            val durationText = if (mins > 0) " • $mins мин" else " • < 1 мин"
            binding.tvWorkoutDate.text = "${workout.dateFormatted}$durationText • Подходов: ${workout.sets.size}"
            
            binding.root.setOnClickListener { onWorkoutClicked(workout.id) }
        }
    }
}
