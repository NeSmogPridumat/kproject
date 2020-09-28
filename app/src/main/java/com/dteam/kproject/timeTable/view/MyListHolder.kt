package com.dteam.kproject.timeTable.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.dteam.kproject.R
import com.dteam.kproject.data.MyTimetable
import java.text.SimpleDateFormat
import java.util.*

class MyListHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val timeTextView: TextView = itemView.findViewById(R.id.time_text_view)
    private val progressImageView: ImageView = itemView.findViewById(R.id.progress_image_view)
    private val dateTextView: TextView = itemView.findViewById(R.id.date_text_view)
    private val constraint: ConstraintLayout = itemView.findViewById(R.id.constraint_layout)

    @SuppressLint("SimpleDateFormat")
    fun bind(myItem: MyTimetable, delete: (Long, Int) -> Unit){
        val calendar = Calendar.getInstance()

        val timeZone = calendar.timeZone
        val format = SimpleDateFormat("EEEE, dd MMMM")
        format.timeZone = timeZone
        val date = format.format(Date(myItem.positions.timeStart*1000))
        dateTextView.text = date
        val timeFormat = SimpleDateFormat("HH mm")
        val time = timeFormat.format(Date(myItem.positions.timeStart*1000))
        timeTextView.text = time
        itemView.setOnClickListener {
            showDeleteDialog(myItem.positions.timeStart, myItem.positions.id, delete)
        }

        if (adapterPosition % 2 != 0) constraint.setBackgroundColor(
            ResourcesCompat.getColor(itemView.resources, R.color.dark_blue, null)
        )
        else constraint.setBackgroundColor(
            ResourcesCompat.getColor(itemView.resources, R.color.dark_blue2, null)
        )
    }

    private fun setProgressBar() {
        val circularProgressDrawable = CircularProgressDrawable(itemView.context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 20f
        circularProgressDrawable.setColorSchemeColors(Color.WHITE)
        circularProgressDrawable.start()
        progressImageView.setImageDrawable(circularProgressDrawable)
        progressImageView.isVisible = true
    }

    private fun showDeleteDialog(date: Long, id: Int, delete: (Long, Int) -> Unit){
        val builder = AlertDialog.Builder(itemView.context).apply {
            setMessage("Отменить?")
            setPositiveButton(R.string.ok) { _, _ ->
                setProgressBar()
                delete(date, id)
            }
            setNegativeButton(R.string.no) {dialog, _ -> dialog.cancel()}
        }
        builder.show()
    }
}