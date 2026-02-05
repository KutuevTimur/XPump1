package com.timur.xpump.ui.workout_details

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.timur.xpump.R
import com.timur.xpump.databinding.FragmentWorkoutDetailsBinding

class WorkoutDetailsFragment : Fragment(R.layout.fragment_workout_details) {

    private val viewModel: WorkoutDetailsViewModel by viewModels()

    private var _binding: FragmentWorkoutDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var setsAdapter: WorkoutSetsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWorkoutDetailsBinding.bind(view)

        // 1) получаем workout_id из аргументов
        val workoutId = arguments?.getLong("workout_id") ?: -1L
        viewModel.init(workoutId)

        val mode = arguments?.getString("mode") ?: "edit"
        val isViewMode = mode == "view"

        if (isViewMode) {
            binding.etWeight.visibility = View.GONE
            binding.etReps.visibility = View.GONE
            binding.btnAddSet.visibility = View.GONE
            binding.btnRemoveSet.visibility = View.GONE
        }

        // 2) заголовок тренировки (лучше из VM, чтобы Storage был источником истины)
        binding.tvWorkoutTitle.text = viewModel.getWorkoutName()

        // 3) настраиваем RecyclerView
        setsAdapter = WorkoutSetsAdapter()
        binding.rvSets.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSets.adapter = setsAdapter

        // 4) первый рендер
        render()

        // 5) добавить подход
        binding.btnAddSet.setOnClickListener {
            val weightText = binding.etWeight.text?.toString()?.trim().orEmpty()
            val repsText = binding.etReps.text?.toString()?.trim().orEmpty()

            val ok = viewModel.addSet(weightText, repsText)
            if (!ok) {
                Toast.makeText(requireContext(), "Введи корректные вес и повторы", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // очистка полей после добавления
            binding.etWeight.text?.clear()
            binding.etReps.text?.clear()

            render()
        }

        // 6) удалить последний подход
        binding.btnRemoveSet.setOnClickListener {
            viewModel.removeSet()
            render()
        }
    }

    private fun render() {
        // обновляем счетчик
        val sets = viewModel.getSets()
        binding.tvSetsCount.text = "Подходы: ${sets.size}"

        // обновляем список
        setsAdapter.submitList(sets)

        // кнопка удаления неактивна, если нечего удалять
        binding.btnRemoveSet.isEnabled = sets.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
