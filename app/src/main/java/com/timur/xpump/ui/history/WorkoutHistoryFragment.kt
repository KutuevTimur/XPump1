package com.timur.xpump.ui.history

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.timur.xpump.R
import com.timur.xpump.ViewModelFactory
import com.timur.xpump.XPumpApp
import com.timur.xpump.databinding.FragmentWorkoutHistoryBinding
import kotlinx.coroutines.launch

class WorkoutHistoryFragment : Fragment(R.layout.fragment_workout_history) {

    private var _binding: FragmentWorkoutHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var workoutAdapter: WorkoutAdapter

    // 1. Подключаем нашу новую ViewModel через Фабрику, передавая ей базу данных
    private val viewModel: WorkoutHistoryViewModel by viewModels {
        ViewModelFactory((requireActivity().application as XPumpApp).repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWorkoutHistoryBinding.bind(view)

        // Настроим адаптер для списка тренировок
        workoutAdapter = WorkoutAdapter { workoutId ->
            val bundle = Bundle().apply {
                putLong("workout_id", workoutId)
                putString("mode", "view")
            }

            // Переход к деталям тренировки, но в режиме только для просмотра
            findNavController().navigate(
                R.id.action_workoutHistoryFragment_to_workoutDetailsFragment,
                bundle
            )
        }

        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = workoutAdapter

        // 2. Подписываемся на "живой" поток данных из базы (Room)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.workouts.collect { list ->
                workoutAdapter.submitList(list) // Адаптер обновится сам, как только в БД что-то изменится
            }
        }

        // --- ПРЕМИУМ СВАЙП ДЛЯ ИСТОРИИ ---
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT
        ) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

            // Рисуем красный фон и корзину ВО ВРЕМЯ свайпа
            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                val itemView = viewHolder.itemView
                // Берем стандартную системную иконку мусорки
                val icon = ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_menu_delete)
                val background = ColorDrawable(Color.parseColor("#FE4A49")) // Сочный красный цвет

                if (dX < 0) { // Если тянем влево
                    background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    background.draw(c)

                    icon?.let {
                        val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                        val iconTop = itemView.top + (itemView.height - it.intrinsicHeight) / 2
                        val iconBottom = iconTop + it.intrinsicHeight
                        val iconLeft = itemView.right - iconMargin - it.intrinsicWidth
                        val iconRight = itemView.right - iconMargin
                        it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        it.draw(c)
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

            // Что происходит, когда дотянули до конца
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val workoutList = workoutAdapter.currentList
                
                if (position in workoutList.indices) {
                    val workoutToDelete = workoutList[position]

                    // Красивый диалог подтверждения
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Удаление тренировки")
                        .setMessage("Удалить «${workoutToDelete.name}» со всеми подходами?")
                        .setPositiveButton("Удалить") { _, _ ->
                            viewModel.deleteWorkout(workoutToDelete.id) // Убиваем в базе
                        }
                        .setNegativeButton("Отмена") { _, _ ->
                            workoutAdapter.notifyItemChanged(position) // АНИМАЦИЯ: Плавный возврат на место
                        }
                        .setOnCancelListener {
                            workoutAdapter.notifyItemChanged(position) // Возврат, если просто кликнули мимо окна
                        }
                        .show()
                }
            }
        }
        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.rvHistory)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
