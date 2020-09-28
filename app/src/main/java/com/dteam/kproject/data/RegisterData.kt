package com.dteam.kproject.data

import com.google.gson.annotations.SerializedName

data class RegisterData(
    @SerializedName("Number")
    val number: String,

    @SerializedName("Name")
    val name: String,

    @SerializedName("Password")
    val password: String
)