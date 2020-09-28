package com.dteam.kproject.database

import androidx.room.TypeConverter
import com.dteam.kproject.data.TimeQueue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TimeQueueConverter {
    @TypeConverter
    fun fromTimeQueue (timeQueue: TimeQueue): String {
        return Gson().toJson(timeQueue)
    }

    @TypeConverter
    fun toTimeQueue(data: String): TimeQueue {
        return Gson().fromJson(data, object : TypeToken<TimeQueue>(){}.type)
    }
}