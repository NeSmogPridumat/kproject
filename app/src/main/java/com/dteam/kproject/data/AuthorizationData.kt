package com.dteam.kproject.data

import com.google.gson.annotations.SerializedName

data class AuthorizationData(
    @SerializedName("Number")
    val number: String,

    @SerializedName("Password")
    val password: String
)