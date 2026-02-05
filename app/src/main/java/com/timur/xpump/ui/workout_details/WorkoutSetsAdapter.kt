package com.timur.xpump.ui.workout_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.timur.xpump.databinding.ItemWorkoutSetBinding
import com.timur.xpump.model.WorkoutSet

class WorkoutSetsAdapter : RecyclerView.Adapter<WorkoutSetsAdapter.VH>() {

    private val items = mutableListOf<WorkoutSet>()


    // Этот метод обновляет список
    fun submitList(list: List<WorkoutSet>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged() // пока просто и понятно
    }


// Метод для создания ViewHolder'а
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemWorkoutSetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    // Связываем данные с ViewHolder'ом
    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position], position)
    }

    // Возвращаем количество элементов в списке
    override fun getItemCount(): Int = items.size


    // ViewHolder для элемента списка
    class VH(private val binding: ItemWorkoutSetBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(set: WorkoutSet, position: Int) {
            binding.tvSetText.text = "${position + 1}) ${set.weight} кг × ${set.reps}"
        }
    }
}

