package com.timur.xpump.ui.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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

        // 3. Кнопка теперь дает команду ViewModel сохранить данные навсегда
        binding.btnAddWorkout.setOnClickListener {
            viewModel.addRandomWorkout()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

