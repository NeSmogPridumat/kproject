package com.dteam.kproject.database

import androidx.room.*
import com.dteam.kproject.data.MyTimetable

@Dao
interface MyTimetableDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMyTimetable(myTimetable: MyTimetable)

    @Delete
    fun deleteMyTimetable(myTimetable: MyTimetable)

    @Query("SELECT * FROM `MyTimetable`")
    suspend fun getAll(): List<MyTimetable>

    @Query("SELECT * FROM `MyTimetable` WHERE date == :date")
    fun getMyTimetableByDate(date: String): List<MyTimetable>

    @Query("DELETE FROM 'MyTimetable'")
    fun deleteAll()
}