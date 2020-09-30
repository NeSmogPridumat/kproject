package com.dteam.kproject.timeTable.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dteam.kproject.MainActivity
import com.dteam.kproject.R
import com.dteam.kproject.timeTable.viewModel.TimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class TimeTableFragment : Fragment() {
    private var chosenDate: Long = 0
    private val currentDateKey = "CurrentDate"
    private val viewModel: TimetableViewModel by viewModels()

    private lateinit var timetableRecyclerView: RecyclerView
    private lateinit var progressBarOverlay: RelativeLayout
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            chosenDate = it.getLong(currentDateKey, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_time_table, container, false)
        timetableRecyclerView = view.findViewById(R.id.timetable_recycler_view)
        progressBarOverlay = view.findViewById(R.id.progressBarOverlay)
        progressBar = view.findViewById(R.id.progressBar)

        val index = getIndex()
        val isToday = itIsToday()
        setDate(isToday)
        getTimetable()

        timetableRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = TimetableAdapter(
                ArrayList(),
                index,
                view.context.getSharedPreferences(MainActivity.preferenceKey, Context.MODE_PRIVATE)
                    .getString(MainActivity.userIdKey, "")!!,
                ::setTimes,
                ::delete
            )
        }

        viewModel.getTimetableLD().observe(this as LifecycleOwner, {
            progressBarOverlay.isVisible = false
            (timetableRecyclerView.adapter as TimetableAdapter).setData(it.positions)
        })

        viewModel.getAnswerSetTimesLD().observe(this as LifecycleOwner, {
            findNavController().popBackStack()
        })

        viewModel.getErrorLiveData().observe(this as LifecycleOwner, {
            it.getContentIfNotHandler()?.let { errorText ->
                Toast.makeText(requireContext(), errorText, Toast.LENGTH_LONG).show()
                timetableRecyclerView.adapter?.notifyDataSetChanged()
            }
        })

        viewModel.getDeleteLD().observe(this as LifecycleOwner, {
            it.getContentIfNotHandler()?.let {
                viewModel.getTimetables(chosenDate / 1000)
            }
        })
        return view
    }

    private fun getTimetable() {
        viewModel.getTimetables(chosenDate / 1000)
        val animation = AnimationUtils.loadAnimation(context, R.anim.progress_rotate)
        progressBar.startAnimation(animation)
        progressBarOverlay.isVisible = true
    }

    @SuppressLint("SimpleDateFormat")
    private fun setDate(isToday: Boolean) {
        val format = SimpleDateFormat("EEE, dd MMMM")
        val date = format.format(Date(chosenDate))
        requireActivity().title =
            if (isToday) resources.getString(R.string.today) + " " + date
            else date
    }

    private fun setTimes(position: Int) {
        viewModel.setTimes(chosenDate, position)
    }

    private fun delete(position: Int) {
        viewModel.delete(chosenDate, position)
    }

    private fun getIndex(): Double {
        val currentHours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return currentHours - 9.toDouble()
    }

    private fun itIsToday(): Boolean {
        val chosenCalendar = Calendar.getInstance()
        chosenCalendar.time = Date(chosenDate)
        return chosenCalendar.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance()
            .get(Calendar.DAY_OF_MONTH)
    }
}