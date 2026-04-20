package com.timur.xpump.ui.workout_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.timur.xpump.databinding.ItemSetBinding
import com.timur.xpump.model.WorkoutSet

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
        holder.bind(items[position], position + 1)
    }

    override fun getItemCount(): Int = items.size

    class SetViewHolder(private val binding: ItemSetBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(workoutSet: WorkoutSet, position: Int) {
            binding.tvSetNumberBadge.text = position.toString()
            binding.tvExerciseName.text = workoutSet.exerciseName
            binding.tvSetDetails.text = "${workoutSet.weight} кг x ${workoutSet.reps} повторений"
            binding.tvSetWeightAndReps.text = "${workoutSet.weight} kg x ${workoutSet.reps}"
        }
    }
}
