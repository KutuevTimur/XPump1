package com.timur.xpump.ui.workout_details

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.timur.xpump.R
import com.timur.xpump.ViewModelFactory
import com.timur.xpump.XPumpApp
import com.timur.xpump.databinding.FragmentWorkoutDetailsBinding
import com.timur.xpump.model.WorkoutSet
import kotlinx.coroutines.launch

class WorkoutDetailsFragment : Fragment(R.layout.fragment_workout_details) {

    // Подключаем ViewModel с базой данных
    private val viewModel: WorkoutDetailsViewModel by viewModels {
        ViewModelFactory((requireActivity().application as XPumpApp).repository)
    }

    private var _binding: FragmentWorkoutDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var setsAdapter: WorkoutSetsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWorkoutDetailsBinding.bind(view)

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

        setsAdapter = WorkoutSetsAdapter()
        binding.rvSets.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSets.adapter = setsAdapter

        // ЖИВОЙ UI: Слушаем изменения из базы данных
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.workoutData.collect { data ->
                if (data != null) {
                    binding.tvWorkoutTitle.text = data.workout.name
                    binding.tvSetsCount.text = "Подходы: ${data.sets.size}"

                    // Превращаем данные из базы в модели для адаптера
                    val mappedSets = data.sets.map { WorkoutSet(it.weight, it.reps) }
                    setsAdapter.submitList(mappedSets)

                    binding.btnRemoveSet.isEnabled = data.sets.isNotEmpty()
                }
            }
        }

        binding.btnAddSet.setOnClickListener {
            val weightText = binding.etWeight.text?.toString()?.trim().orEmpty()
            val repsText = binding.etReps.text?.toString()?.trim().orEmpty()

            if (!viewModel.addSet(weightText, repsText)) {
                Toast.makeText(requireContext(), "Введи корректные вес и повторы", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.etWeight.text?.clear()
            binding.etReps.text?.clear()
        }

        binding.btnRemoveSet.setOnClickListener {
            viewModel.removeSet()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}