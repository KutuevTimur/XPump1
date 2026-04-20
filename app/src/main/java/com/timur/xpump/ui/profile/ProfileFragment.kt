package com.timur.xpump.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.timur.xpump.R
import com.timur.xpump.ViewModelFactory
import com.timur.xpump.XPumpApp
import com.timur.xpump.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // Подключаем ViewModel к настоящей БД
    private val viewModel: ProfileViewModel by viewModels {
        ViewModelFactory((requireActivity().application as XPumpApp).repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        // Обновляем текст на экране на основе базы данных
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.tvProfileLevel.text = "Уровень: ${state.level}"
                binding.tvProfileXp.text = "Опыт: ${state.totalXp}/100"
                binding.tvWorkoutCount.text = "Тренировок: ${state.workoutCount}"
                binding.tvBenchPressPRValue.text = state.benchPressRank
                binding.tvSquatPRValue.text = state.squatRank
            }
        }

        binding.btnStartWorkout.setOnClickListener {
            // Пишем в настоящую БД, а не в оперативку!
            viewModel.createNewWorkout("Новая тренировка") { newWorkoutId ->
                val bundle = Bundle().apply {
                    putLong("workout_id", newWorkoutId)
                    putString("mode", "edit")
                }
                findNavController().navigate(R.id.action_profileFragment_to_workoutDetailsFragment, bundle)
            }
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
