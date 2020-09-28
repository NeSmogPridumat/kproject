package com.dteam.kproject.timeTable.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dteam.kproject.R
import com.dteam.kproject.data.MyTimetable

class MyListAdapter(
    var myItems: ArrayList<MyTimetable>,
    private val delete: (Long, Int) -> Unit
): RecyclerView.Adapter<MyListHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyListHolder {
        return MyListHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.my_item_holder_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyListHolder, position: Int) {
        holder.bind(myItems[position], delete)
    }

    override fun getItemCount(): Int = myItems.size
}