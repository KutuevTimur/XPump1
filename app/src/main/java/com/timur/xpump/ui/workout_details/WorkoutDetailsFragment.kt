package com.timur.xpump.ui.workout_details

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
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
import com.timur.xpump.databinding.FragmentWorkoutDetailsBinding
import com.timur.xpump.model.WorkoutSet
import com.timur.xpump.utils.TimeMaskWatcher
import kotlinx.coroutines.launch

class WorkoutDetailsFragment : Fragment(R.layout.fragment_workout_details) {

    private val viewModel: WorkoutDetailsViewModel by viewModels {
        ViewModelFactory((requireActivity().application as XPumpApp).repository)
    }

    private var _binding: FragmentWorkoutDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var setsAdapter: WorkoutSetsAdapter
    
    private var restCountDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWorkoutDetailsBinding.bind(view)

        val workoutId = arguments?.getLong("workout_id") ?: -1L
        viewModel.init(workoutId)

        val mode = arguments?.getString("mode") ?: "edit"
        val isViewMode = mode == "view"

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (!isViewMode) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Тренировка в процессе")
                    .setMessage("Выйти из экрана? Таймер продолжит тикать в фоне.")
                    .setPositiveButton("Выйти") { _, _ -> isEnabled = false; requireActivity().onBackPressed() }
                    .setNegativeButton("Остаться", null)
                    .show()
            } else {
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }

        // Access the partial layout views
        val inputBinding = binding.includePartialInputCard

        if (isViewMode) {
            // Скрываем вообще всё, что позволяет вводить данные
            inputBinding.tilExerciseName.visibility = View.GONE
            inputBinding.tilWeight.visibility = View.GONE
            inputBinding.tilReps.visibility = View.GONE
            inputBinding.tilDefaultRest.visibility = View.GONE
            inputBinding.btnAddSet.visibility = View.GONE
            binding.btnFinishWorkout.visibility = View.GONE
            
            binding.tvWorkoutTitle.append(" (Архив)")
        } else {
            val suggestions = arrayOf(
                "Жим лежа", "Приседания", "Становая тяга", "Подтягивания", 
                "Брусья", "Разводка гантелей", "Тяга блока", "Планка", "Бег", "Эллипс", "Велосипед", "Стульчик"
            )
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                suggestions
            )
            inputBinding.etExerciseName.setAdapter(adapter)
            inputBinding.etExerciseName.threshold = 1
            
            // Идеальная маска для времени подхода
            inputBinding.etTime.addTextChangedListener(TimeMaskWatcher(inputBinding.etTime))
            
            // Идеальная маска для времени отдыха по умолчанию
            inputBinding.etDefaultRest.addTextChangedListener(TimeMaskWatcher(inputBinding.etDefaultRest))
            inputBinding.etDefaultRest.addTextChangedListener { text ->
                viewModel.updateDefaultRestTime(text.toString())
            }

            inputBinding.tilWeight.visibility = View.VISIBLE
            inputBinding.tilReps.visibility = View.VISIBLE
            inputBinding.tilDistance.visibility = View.GONE
            inputBinding.tilTime.visibility = View.GONE
            
            inputBinding.etExerciseName.addTextChangedListener { text ->
                val name = text?.toString()?.lowercase() ?: ""
                when {
                    name.contains("бег") || name.contains("эллипс") || name.contains("велосипед") -> {
                        inputBinding.tilWeight.visibility = View.GONE
                        inputBinding.tilReps.visibility = View.GONE
                        inputBinding.tilDistance.visibility = View.VISIBLE
                        inputBinding.tilTime.visibility = View.VISIBLE
                    }
                    name.contains("планка") || name.contains("стульчик") -> {
                        inputBinding.tilWeight.visibility = View.GONE
                        inputBinding.tilReps.visibility = View.GONE
                        inputBinding.tilDistance.visibility = View.GONE
                        inputBinding.tilTime.visibility = View.VISIBLE
                    }
                    else -> {
                        inputBinding.tilWeight.visibility = View.VISIBLE
                        inputBinding.tilReps.visibility = View.VISIBLE
                        inputBinding.tilDistance.visibility = View.GONE
                        inputBinding.tilTime.visibility = View.GONE
                    }
                }
            }
        }

        setsAdapter = WorkoutSetsAdapter()
        binding.rvSets.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSets.adapter = setsAdapter

        // ЖИВОЙ UI
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.workoutData.collect { data ->
                if (data != null) {
                    binding.tvWorkoutTitle.text = data.workout.name
                    binding.tvSetsCount.text = "Подходов: ${data.sets.size}"

                    if (!isViewMode) {
                        val startTime = data.workout.startTime
                        val currentTime = System.currentTimeMillis()
                        val elapsedMillis = currentTime - startTime
                        
                        binding.workoutChronometer.base = SystemClock.elapsedRealtime() - elapsedMillis
                        binding.workoutChronometer.start()
                    }

                    val mappedSets = data.sets.map {
                        WorkoutSet(id = it.id, weight = it.weight, reps = it.reps, exerciseName = it.exerciseName ?: "Упражнение", setType = it.setType, timeSeconds = it.timeSeconds, distance = it.distance)
                    }
                    setsAdapter.submitList(mappedSets)
                }
            }
        }

        inputBinding.btnAddSet.setOnClickListener {
            val exerciseName = inputBinding.etExerciseName.text?.toString()?.trim().orEmpty()
            val weightText = inputBinding.etWeight.text?.toString()?.trim().orEmpty()
            val repsText = inputBinding.etReps.text?.toString()?.trim().orEmpty()
            val distanceText = inputBinding.etDistance.text?.toString()?.trim().orEmpty()
            val timeText = inputBinding.etTime.text?.toString()?.trim().orEmpty()
            
            val setType = when (inputBinding.cgSetType.checkedChipId) {
                R.id.chipWarmup -> "WARMUP"
                R.id.chipDropset -> "DROPSET"
                R.id.chipFailure -> "FAILURE"
                else -> "NORMAL"
            }

            val distance = distanceText.toDoubleOrNull() ?: 0.0
            val timeSeconds = com.timur.xpump.utils.ExerciseUtils.parseMMSSToSeconds(timeText)

            if (!viewModel.addSet(weightText, repsText, exerciseName, setType, timeSeconds, distance)) {
                Toast.makeText(requireContext(), "Заполни нужные поля!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            inputBinding.etWeight.text?.clear()
            inputBinding.etReps.text?.clear()
            inputBinding.etDistance.text?.clear()
            inputBinding.etTime.text?.clear()
            inputBinding.etWeight.requestFocus()
            
            val restMillis = viewModel.defaultRestTimeSeconds * 1000L
            startRestTimer(restMillis)
        }

        binding.btnRestSkip.setOnClickListener {
            restCountDownTimer?.cancel()
            binding.restTimerCard.visibility = View.GONE
        }
        binding.btnRestPlus.setOnClickListener {
            startRestTimer(timeLeftInMillis + 30000)
        }
        binding.btnRestMinus.setOnClickListener {
            val newTime = timeLeftInMillis - 30000
            if (newTime > 0) startRestTimer(newTime) else {
                restCountDownTimer?.cancel()
                binding.restTimerCard.visibility = View.GONE
            }
        }

        binding.btnFinishWorkout.setOnClickListener {
            binding.workoutChronometer.stop()
            val elapsedMillis = SystemClock.elapsedRealtime() - binding.workoutChronometer.base
            val durationSeconds = elapsedMillis / 1000

            viewModel.finishWorkout(durationSeconds)
            Toast.makeText(requireContext(), "Тренировка сохранена!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        // --- ПРЕМИУМ СВАЙП ДЛЯ ПОДХОДОВ ---
        if (!isViewMode) {
            val swipeSetCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

                override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                    val itemView = viewHolder.itemView
                    val icon = ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_menu_delete)
                    val background = ColorDrawable(Color.parseColor("#FE4A49"))

                    if (dX < 0) {
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

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val setList = setsAdapter.currentList
                    if (position in setList.indices) {
                        val setToDelete = setList[position]

                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Удаление подхода")
                            .setMessage("Удалить этот подход?")
                            .setPositiveButton("Удалить") { _, _ ->
                                viewModel.deleteSpecificSet(setToDelete.id)
                            }
                            .setNegativeButton("Отмена", null)
                            .setOnCancelListener {
                                setsAdapter.notifyItemChanged(position)
                            }
                            .show()
                    }
                }
            }
            ItemTouchHelper(swipeSetCallback).attachToRecyclerView(binding.rvSets)
        }
    }

    private fun startRestTimer(timeInMillis: Long) {
        restCountDownTimer?.cancel()
        timeLeftInMillis = timeInMillis
        binding.restTimerCard.visibility = View.VISIBLE

        // Флаг, чтобы предупреждение сработало ровно 1 раз
        var isPreAlertFired = false 

        restCountDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                val totalSeconds = (timeLeftInMillis / 1000)
                val minutes = totalSeconds / 60
                val seconds = totalSeconds % 60
                
                binding.tvRestTimer.text = String.format("%02d:%02d", minutes, seconds)

                // СИСТЕМА ПРЕДУПРЕЖДЕНИЯ (РОВНО ЗА 5 СЕКУНД)
                if (totalSeconds == 5L && !isPreAlertFired) {
                    isPreAlertFired = true
                    vibratePreAlert() // Та самая двойная пульсация
                }
            }

            override fun onFinish() {
                vibratePhone() // Мощный финальный вибросигнал
                binding.tvRestTimer.text = "Пора!"
                binding.tvRestTimer.postDelayed({ 
                    binding.restTimerCard.visibility = View.GONE 
                }, 2000)
            }
        }.start()
    }

    private fun vibratePhone() {
        val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(500)
        }
    }

    private fun vibratePreAlert() {
        val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        // Паттерн: ждать 0мс, вибрировать 100мс, пауза 100мс, вибрировать 100мс
        val pattern = longArrayOf(0, 100, 100, 100)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }

    override fun onDestroyView() {
        restCountDownTimer?.cancel()
        super.onDestroyView()
        _binding = null
    }
}
