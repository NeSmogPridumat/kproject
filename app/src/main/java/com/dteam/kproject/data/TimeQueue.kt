package com.dteam.kproject.data

import com.google.gson.annotations.SerializedName

data class TimeQueue(
    val id: Int,
    @SerializedName("TimeStart")
    val timeStart: Long,

    @SerializedName("TimeEnd")
    val timeEnd: Long,

    @SerializedName("User")
    val user: User,
)