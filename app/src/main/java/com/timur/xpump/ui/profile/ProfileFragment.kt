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

        viewLifecycleOwner.lifecycleScope.launch {
            // Note: Since ProfileViewModel doesn't expose the actual workout object,
            // we'll rely on the existing logic or adjust the ViewModel if needed.
            // Based on instructions, using the ID from activeWorkoutId.
            viewModel.activeWorkoutId.collect { id ->
                if (id != null) {
                    binding.btnStartWorkout.text = "Продолжить тренировку"
                    binding.btnStartWorkout.setOnClickListener {
                        navigateToWorkout(id)
                    }
                } else {
                    binding.btnStartWorkout.text = "Начать тренировку"
                    binding.btnStartWorkout.setOnClickListener {
                        showStartDialog()
                    }
                }
            }
        }

        binding.btnOpenHistory.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_workoutHistoryFragment)
        }
    }

    private fun navigateToWorkout(id: Long) {
        val bundle = Bundle().apply {
            putLong("workout_id", id)
            putString("mode", "edit")
        }
        findNavController().navigate(R.id.action_profileFragment_to_workoutDetailsFragment, bundle)
    }

    private fun showStartDialog() {
        // 1. Создаем поле ввода прямо в коде
        val input = android.widget.EditText(requireContext())
        input.hint = "Напр: День груди или Тяга"
        input.setPadding(60, 40, 60, 40) // Немного отступов для красоты

        // 2. Строим стандартный диалог Android
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Как назовем тренировку?")
            .setMessage("Оставь пустым, чтобы использовать дату")
            .setView(input)
            .setPositiveButton("Начать") { _, _ ->
                val enteredName = input.text.toString().trim()

                // Если имя пустое, передаем пустую строку, логика отображения сработает в истории
                val finalName = if (enteredName.isEmpty()) "Тренировка" else enteredName

                viewModel.createNewWorkout(finalName) { newWorkoutId ->
                    navigateToWorkout(newWorkoutId)
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
