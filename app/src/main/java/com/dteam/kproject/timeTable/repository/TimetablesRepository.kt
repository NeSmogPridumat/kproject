package com.dteam.kproject.timeTable.repository

import com.dteam.kproject.data.MyTimetable
import com.dteam.kproject.data.SetTimesData
import com.dteam.kproject.data.Timetable
import com.dteam.kproject.database.MyTimetableDao
import com.dteam.kproject.http.RetrofitHelper
import kotlinx.coroutines.*
import org.json.JSONObject
import javax.inject.Inject

@Suppress("BlockingMethodInNonBlockingContext")
class TimetablesRepository @Inject constructor(
    private val myTimetableDao: MyTimetableDao
) {

    fun getTimeTablesAsync(timeSecond: Long): Deferred<Timetable>
            = CoroutineScope(Dispatchers.IO).async{
        val response = RetrofitHelper.makeRetrofitService().getTableTimes(timeSecond)
        when {
            response.isSuccessful -> return@async response.body()!!
            else -> {
                val jo = JSONObject(response.errorBody()!!.string())
                throw Throwable(jo.getString("msg"))
            }
        }
    }

    fun setTimesAsync(setTimesData: SetTimesData): Deferred<Any>
            = CoroutineScope(Dispatchers.IO).async {
        val response = RetrofitHelper.makeRetrofitService().setTimes(setTimesData)
        when {
            response.isSuccessful -> return@async response.body()!!
            else -> {
                val jo = JSONObject(response.errorBody()!!.string())
                throw Throwable(jo.getString("msg"))
            }
        }
    }

    fun deleteAsync(setTimesData: SetTimesData): Deferred<Any>
            = CoroutineScope(Dispatchers.IO).async {
        val response = RetrofitHelper.makeRetrofitService().delete(setTimesData)
        when {
            response.isSuccessful -> return@async response.body()!!
            else -> {
                val jo = JSONObject(response.errorBody()!!.string())
                throw Throwable(jo.getString("msg"))
            }
        }
    }

    fun searchAsync(id: String, time: Long): Deferred<ArrayList<MyTimetable>>
            = CoroutineScope(Dispatchers.IO).async {
        val response = RetrofitHelper
            .makeRetrofitService().search(id, time)
        when {
            response.isSuccessful -> return@async response.body()?: ArrayList()
            else -> {
                val jo = JSONObject(response.errorBody()!!.string())
                throw Throwable(jo.getString("msg"))
            }
        }
    }

    fun saveInDB(myTimetable: MyTimetable) = CoroutineScope(Dispatchers.Default).launch{
        myTimetableDao.insertMyTimetable(myTimetable)
    }
}