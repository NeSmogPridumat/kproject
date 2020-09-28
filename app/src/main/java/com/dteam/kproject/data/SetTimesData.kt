package com.dteam.kproject.data

import com.google.gson.annotations.SerializedName

data class SetTimesData(
    @SerializedName("UserId")
    val userId: String,

    @SerializedName("Date")
    val date: String,

    @SerializedName("Position")
    val position: Int
)