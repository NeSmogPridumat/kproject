package com.dteam.kproject.timeTable.view

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dteam.kproject.MainActivity
import com.dteam.kproject.R
import com.dteam.kproject.data.MyTimetable
import com.dteam.kproject.timeTable.viewModel.TimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CalendarFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var myListRecyclerView: RecyclerView

    private val currentDateParams = "CurrentDate"
    private val viewModel: TimetableViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        calendarView = view.findViewById(R.id.calendar_view)
        calendarView.minDate = Calendar.getInstance().timeInMillis - 1000
        calendarView.maxDate = Calendar.getInstance().timeInMillis + (604800 * 4)*1000L
        calendarView.setOnDateChangeListener { _, year, month, day ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, day)
            val milliTime = calendar.timeInMillis
            val bundle = Bundle()
            bundle.putLong(currentDateParams, milliTime)
            findNavController().navigate(R.id.action_calendarFragment_to_timeTableFragment, bundle)
        }

        myListRecyclerView = view.findViewById(R.id.my_list_recycler_view)
        setHasOptionsMenu(true)
        setTitle()

        viewModel.search()
        viewModel.getMyListLD().observe(this as LifecycleOwner, {
            println(it.toString())
            setMyList(it)
        })

        viewModel.getDeleteLD().observe(this as LifecycleOwner, {
            it.getContentIfNotHandler()?.let {
                viewModel.search()
            }
        })

        viewModel.getErrorLiveData().observe(this as LifecycleOwner, {
            it.getContentIfNotHandler()?.let { errorText ->
                Toast.makeText(requireContext(), errorText, Toast.LENGTH_LONG).show()
            }
        })
        return view
    }

    private fun setMyList(list: ArrayList<MyTimetable>) {
        myListRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = MyListAdapter(list, ::delete)
        }
    }

    private fun setTitle() {
        try {
            requireActivity().title = resources.getString(R.string.app_name)
        } catch (t: Throwable){
            t.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.exit_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exit_menu_item -> showExitDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun exitApp() {
        requireContext().getSharedPreferences(
            MainActivity.preferenceKey, Context.MODE_PRIVATE).edit().clear().apply()
        val options = NavOptions.Builder()
            .setPopUpTo(R.id.calendarFragment, true).build()
        findNavController().navigate(R.id.setPhoneFragment, null, options)
    }

    private fun showExitDialog(){
        val builder = AlertDialog.Builder(requireContext()).apply {
            setMessage("Вы уверены, что хотите выйти из профиля?")
            setTitle(R.string.exit)
            setPositiveButton(R.string.ok) { _, _ -> exitApp() }
            setNegativeButton(R.string.no) {dialog, _ -> dialog.cancel()}
        }
        builder.show()
    }

    private fun showDeleteDialog(date: Long, id: Int){
        val builder = AlertDialog.Builder(requireContext()).apply {
            setMessage("Отменить?")
            setPositiveButton(R.string.ok) { _, _ ->
                delete(date, id)
            }
            setNegativeButton(R.string.no) {dialog, _ -> dialog.cancel()}
        }
        builder.show()
    }

    private fun delete(date: Long, id: Int) {
        viewModel.delete(date * 1000, id)
    }
}