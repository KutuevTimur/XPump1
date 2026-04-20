package com.timur.xpump.ui.workout_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.timur.xpump.databinding.ItemWorkoutSetBinding
import com.timur.xpump.model.WorkoutSet

class WorkoutSetsAdapter : RecyclerView.Adapter<WorkoutSetsAdapter.VH>() {

    private val items = mutableListOf<WorkoutSet>()

    fun submitList(list: List<WorkoutSet>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemWorkoutSetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    class VH(private val binding: ItemWorkoutSetBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(set: WorkoutSet, position: Int) {
            binding.tvSetNumber.text = (position + 1).toString()
            binding.tvSetName.text = set.exerciseName
            binding.tvSetInfo.text = "${set.weight} кг x ${set.reps}"
        }
    }
}
