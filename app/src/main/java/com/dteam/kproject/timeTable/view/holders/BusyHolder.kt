package com.dteam.kproject.timeTable.view.holders

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.dteam.kproject.R
import com.dteam.kproject.data.TimeQueue

class BusyHolder (itemView: View): RecyclerView.ViewHolder(itemView){

    private val timeTextView: TextView = itemView.findViewById(R.id.time_text_view)
    private val nameTextView: TextView = itemView.findViewById(R.id.name_text_view)
    private val constraint: ConstraintLayout = itemView.findViewById(R.id.constraint_layout)

    fun bind(timeQueue: TimeQueue, time: String) {
        timeTextView.text = time
        nameTextView.text = timeQueue.user.name
        if (adapterPosition % 2 != 0) constraint.setBackgroundColor(ResourcesCompat.getColor(itemView.resources, R.color.dark_blue, null))
        else constraint.setBackgroundColor(ResourcesCompat.getColor(itemView.resources, R.color.dark_blue2, null))
    }
}