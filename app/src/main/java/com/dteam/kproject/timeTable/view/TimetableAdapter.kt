package com.dteam.kproject.timeTable.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dteam.kproject.R
import com.dteam.kproject.data.TimeQueue
import com.dteam.kproject.timeTable.view.holders.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TimetableAdapter(
    private  var timetable: ArrayList<TimeQueue>,
    var index: Double,
    private val userId: String,
    private val setTimes: (Int) -> Unit,
    private val delete: (Int) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val POST_TYPE = 1
    private val BUSY_TYPE = 2
    private val FREE_TYPE = 3
    private val YOUR_TYPE = 4
    private val defaultId = "000000000000000000000000"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType){
            POST_TYPE -> PastHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.past_holder_layout, parent, false)
            )
            BUSY_TYPE -> BusyHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.busy_holder_layout, parent, false)
            )
            FREE_TYPE -> FreeHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.free_holder_layout, parent, false)
            )
            else -> MyHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.my_holder_layout, parent, false)
            )
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val format = SimpleDateFormat("HH.mm")
        val calendar = Calendar.getInstance()
        calendar.set(0, 0,0,9,0)
        calendar.add(Calendar.HOUR_OF_DAY, position)
        when (holder) {
            is FreeHolder -> holder.bind(timetable[position], setTimes, format.format(calendar.time))
            is MyHolder -> holder.bind( delete, format.format(calendar.time), timetable[position].timeStart * 1000)
            is BusyHolder -> holder.bind(timetable[position], format.format(calendar.time))
            is PastHolder -> holder.bind(format.format(calendar.time))
        }
    }

    override fun getItemCount(): Int = timetable.size

    override fun getItemViewType(position: Int): Int {
        val timeQueue = timetable[position]
        return when {
            timeQueue.user.id == userId -> YOUR_TYPE
            timeQueue.user.id != defaultId -> BUSY_TYPE
                    Calendar.getInstance().timeInMillis > timeQueue.timeStart * 1000 -> POST_TYPE
            else -> FREE_TYPE
        }
    }

    fun setData (newTimetable: ArrayList<TimeQueue>){
        timetable = newTimetable
        notifyDataSetChanged()
    }
}