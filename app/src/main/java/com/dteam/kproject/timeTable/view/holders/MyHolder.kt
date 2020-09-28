package com.dteam.kproject.timeTable.view.holders

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
import com.dteam.kproject.data.TimeQueue

class MyHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val timeTextView: TextView = itemView.findViewById(R.id.time_text_view)
    private val progressImageView: ImageView = itemView.findViewById(R.id.progress_image_view)
    private val constraint: ConstraintLayout = itemView.findViewById(R.id.constraint_layout)

    fun bind(listener: (Int) -> Unit?, time: String) {
        timeTextView.text = time
        progressImageView.setImageDrawable(
            ResourcesCompat.getDrawable(
                itemView.resources,
                R.drawable.ic_iconfinder_close,
                null
            )
        )
//        progressImageView.isVisible = false
        itemView.setOnClickListener {
            showDeleteDialog(adapterPosition, listener)
        }

        if (adapterPosition % 2 != 0) constraint.setBackgroundColor(ResourcesCompat.getColor(itemView.resources, R.color.dark_blue, null))
        else constraint.setBackgroundColor(ResourcesCompat.getColor(itemView.resources, R.color.dark_blue2, null))
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

    private fun showDeleteDialog(id: Int, delete: (Int) -> Unit?){
        val builder = AlertDialog.Builder(itemView.context).apply {
            setMessage("Отменить?")
            setPositiveButton(R.string.ok) { _, _ ->
                setProgressBar()
                delete(id)
            }
            setNegativeButton(R.string.no) {dialog, _ -> dialog.cancel()}
        }
        builder.show()
    }
}