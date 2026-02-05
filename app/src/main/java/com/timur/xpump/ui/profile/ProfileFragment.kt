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

        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.workoutCount.collectLatest { count ->
                binding.tvProfileInfo.text = count.toString()
            }
        }

        // показать текущее значение
        //binding.tvCount.text = viewModel.workoutCount.toString()


        binding.btnStartWorkout.setOnClickListener {

            val workout = WorkoutStorage.createWorkout("Новая тренировка")

            val bundle = Bundle().apply {
                putLong("workout_id", workout.id)
                putString("workout_name", workout.name)
                putString("mode", "edit")
            }

            findNavController().navigate(
                R.id.action_profileFragment_to_workoutDetailsFragment,
                bundle
            )
        }

        binding.btnOpenHistory.setOnClickListener {
            findNavController().navigate(
                R.id.action_profileFragment_to_workoutHistoryFragment
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
