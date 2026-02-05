package com.timur.xpump.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.timur.xpump.databinding.ItemWorkoutBinding
import com.timur.xpump.model.Workout

class WorkoutAdapter(
    private val onClick: (Long) -> Unit // callback для перехода в детали тренировки
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    private val items = mutableListOf<Workout>()

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
            binding.tvSetsCount.text = "Подходов: ${workout.sets.size}"

            // По клику на тренировку открываем WorkoutDetailsFragment
            binding.root.setOnClickListener {
                onClick(workout.id)
            }
        }
    }
}

