package com.dteam.kproject.data

import com.google.gson.annotations.SerializedName

data class User(
    val id: String,

    @SerializedName("Number")
    val number: String,

    @SerializedName("Name")
    val name: String
)