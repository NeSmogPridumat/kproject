package com.dteam.kproject.data

import com.google.gson.annotations.SerializedName

data class MyTimetable(
    val id: String,

    @SerializedName("Date")
    val date: String,

    @SerializedName("Positions")
    val positions: TimeQueue
)