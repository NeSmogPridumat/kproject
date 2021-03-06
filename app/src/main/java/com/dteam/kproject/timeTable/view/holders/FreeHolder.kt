package com.dteam.kproject.timeTable.view.holders

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.dteam.kproject.R
import com.dteam.kproject.data.TimeQueue

class FreeHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val defaultId = "000000000000000000000000"
    private val timeTextView: TextView = itemView.findViewById(R.id.time_text_view)
    private val progressImageView: ImageView = itemView.findViewById(R.id.progress_image_view)


    fun bind(timeQueue: TimeQueue, listener: (Int) -> Unit?, time: String) {
        timeTextView.text = time
        progressImageView.isVisible = false
        if(timeQueue.user.id == defaultId){
            itemView.setOnClickListener {
                setProgressBar()

                //setTimes
                listener(adapterPosition)
            }
        }
    }

    private fun setProgressBar() {
        val circularProgressDrawable = CircularProgressDrawable(itemView.context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.setColorSchemeColors(Color.GREEN)
        circularProgressDrawable.start()
        progressImageView.setImageDrawable(circularProgressDrawable)
        progressImageView.isVisible = true
    }
}