package com.dteam.kproject.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dteam.kproject.database.TimeQueueConverter
import com.google.gson.annotations.SerializedName

@Entity
data class MyTimetable(
    @field:PrimaryKey(autoGenerate = false)
    val id: String,

    @SerializedName("Date")
    val date: String,

    @TypeConverters(TimeQueueConverter::class)
    @SerializedName("Positions")
    val positions: TimeQueue
)
