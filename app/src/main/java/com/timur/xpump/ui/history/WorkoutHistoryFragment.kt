package com.timur.xpump.ui.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.timur.xpump.R
import com.timur.xpump.databinding.FragmentWorkoutHistoryBinding
import com.timur.xpump.data.WorkoutStorage
import com.timur.xpump.ui.workout_details.WorkoutDetailsFragment

class WorkoutHistoryFragment : Fragment(R.layout.fragment_workout_history) {

    private var _binding: FragmentWorkoutHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var workoutAdapter: WorkoutAdapter

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

        // Загружаем все тренировки из WorkoutStorage
        workoutAdapter.submitList(WorkoutStorage.getAllWorkouts())

        // Временно добавляем тренировку, чтобы показать её в истории
        binding.btnAddWorkout.setOnClickListener {
            val workout = WorkoutStorage.createWorkout("Тренировка ${System.currentTimeMillis()}")
            workoutAdapter.submitList(WorkoutStorage.getAllWorkouts()) // обновляем список
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
