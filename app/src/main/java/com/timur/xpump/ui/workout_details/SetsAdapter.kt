package com.timur.xpump.ui.workout_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.timur.xpump.databinding.ItemSetBinding

class SetsAdapter : RecyclerView.Adapter<SetsAdapter.SetViewHolder>() {

    private val items = mutableListOf<String>() // список строк

    // Этот метод обновляет список, который будет показываться в RecyclerView
    fun submitList(newList: List<String>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged() // пока так, просто и понятно
    }

    //Метод для создания ViewHolder'а, который будет "картинкой" для каждой строки
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {
        val binding = ItemSetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SetViewHolder(binding)
    }
    // Связываем данные с ViewHolder'ом
    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        holder.bind(items[position])
    }

    // Получаем размер списка
    override fun getItemCount(): Int = items.size


    // Внутренний ViewHolder, который будет показывать одну строку
    class SetViewHolder(private val binding: ItemSetBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(text: String) {
            binding.tvSetText.text = text
        }
    }
}
