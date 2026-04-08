package com.timur.xpump.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.timur.xpump.R
import com.timur.xpump.databinding.FragmentProfileBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.timur.xpump.data.WorkoutStorage

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        // Подписываемся на состояние (UDF подход из твоего плана) [cite: 90]
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.tvProfileInfo.text = "Уровень: ${state.level}\nXP: ${state.totalXp}/100"
                // Если есть прогресс-бар в xml, можно добавить state.totalXp
            }
        }

        // Обновляем данные при входе на экран
        viewModel.refreshProfile()

        binding.btnStartWorkout.setOnClickListener {
            // Твой текущий код запуска тренировки
            val workout = WorkoutStorage.createWorkout("Тренировка #${WorkoutStorage.getAllWorkouts().size + 1}")
            val bundle = Bundle().apply {
                putLong("workout_id", workout.id)
                putString("mode", "edit")
            }
            findNavController().navigate(R.id.action_profileFragment_to_workoutDetailsFragment, bundle)
        }

        binding.btnOpenHistory.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_workoutHistoryFragment)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
