package com.dteam.kproject.timeTable.viewModel

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dteam.kproject.Event
import com.dteam.kproject.MainActivity
import com.dteam.kproject.NotificationReceiver
import com.dteam.kproject.R
import com.dteam.kproject.data.MyTimetable
import com.dteam.kproject.data.SetTimesData
import com.dteam.kproject.data.Timetable
import com.dteam.kproject.timeTable.repository.TimetablesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TimetableViewModel @ViewModelInject constructor(
    application: Application,
    private val repository: TimetablesRepository
): AndroidViewModel(application) {

    private var checkIsMy:Boolean = false

    //для получения списка
    private val timetableLiveData by lazy {
        MutableLiveData<Timetable>()
    }
    fun getTimetableLD(): LiveData<Timetable> = timetableLiveData

    private val answerSetTimesLiveData by lazy {
        MutableLiveData<Any>()
    }
    fun getAnswerSetTimesLD(): LiveData<Any> = answerSetTimesLiveData

    //LiveData для отображения ошибок
    private val errorLiveData by lazy {
        MutableLiveData<Event<String>>()
    }
    fun getErrorLiveData(): LiveData<Event<String>> = errorLiveData

    //для моих броней
    private val myListLiveData by lazy {
        MutableLiveData<ArrayList<MyTimetable>>()
    }
    fun getMyListLD(): LiveData<ArrayList<MyTimetable>> = myListLiveData

    private val deleteLiveData by lazy {
        MutableLiveData<Event<Any>>()
    }
    fun getDeleteLD(): LiveData<Event<Any>> = deleteLiveData

    fun getTimetables(time:Long) = CoroutineScope(Dispatchers.Default).launch {
        try {
            val timetable = repository.getTimeTablesAsync(time).await()
            println(timetable.date)
            checkIsMy = timetable.positions.any { it.user.id == getUserId() }
            timetableLiveData.postValue(timetable)
        } catch (t: Throwable){
            t.printStackTrace()
            errorLiveData.postValue(Event(t.message ?:
                (getApplication() as Context).resources.getString(R.string.error)))
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun setTimes(date: Long, position: Int)= CoroutineScope(Dispatchers.Default).launch{
        try {
            if (!checkIsMy) {
                val format = SimpleDateFormat("yyyy-MM-dd")
                val formatDate = format.format(date)
                repository.setTimesAsync(SetTimesData(getUserId(), formatDate, position)).await()
            } else errorLiveData
                .postValue(Event(
                    (getApplication() as Context).resources
                        .getString(R.string.unfortunately_only_one_appointment_per_day_is_possible)
                ))

            getTimetables(date/1000)
            setAlarm(date, position)
        } catch (t: Throwable){
            t.printStackTrace()
            errorLiveData.postValue(Event(t.message ?:
                    (getApplication() as Context).resources.getString(R.string.error)))
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setAlarm(date: Long, position: Int) {
        val calendar = Calendar.getInstance()
        val thisCalendar = Calendar.getInstance()
        thisCalendar.timeInMillis = date
        calendar.set(thisCalendar.get(Calendar.YEAR), thisCalendar.get(Calendar.MONTH), thisCalendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
        val alarmTime = ((((9 + position * 0.5) * 60) - 10)*60*1000).toLong()
        calendar.timeInMillis = calendar.timeInMillis + alarmTime
        println("${calendar.get(Calendar.YEAR)} ${calendar.get(Calendar.MONTH) + 1} ${calendar.get(Calendar.DAY_OF_MONTH)} ${calendar.get(Calendar.HOUR_OF_DAY)} ${calendar.get(Calendar.MINUTE)}" )
        val alarmManager = (getApplication() as Context).getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = createIntent()
        val pendingIntent = PendingIntent.getBroadcast(getApplication(), date.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            println("Build.VERSION_CODES.M")
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent )
        } else alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent )
        val format = SimpleDateFormat("yyyy-MM-dd_hh-mm")
        println(format.format(Date(calendar.timeInMillis)))
    }

    private fun createIntent():Intent {
        return Intent(getApplication(), NotificationReceiver::class.java)
    }

    @SuppressLint("SimpleDateFormat")
    fun delete(date: Long, position: Int) = CoroutineScope(Dispatchers.Default).launch {
        try {
            val format = SimpleDateFormat("yyyy-MM-dd")
            val formatDate = format.format(date)
            val answer = repository.deleteAsync(SetTimesData(getUserId(), formatDate, position) ).await()
            deleteLiveData.postValue(Event(answer))
        } catch (t:Throwable){
            t.printStackTrace()
            errorLiveData.postValue(Event(t.message ?:
                    (getApplication() as Context).resources.getString(R.string.error)))
        }
    }

    private fun getUserId(): String{
        return (getApplication() as Context).getSharedPreferences(
            MainActivity.preferenceKey, Context.MODE_PRIVATE).getString(MainActivity.userIdKey, "")!!
    }

    fun search () = CoroutineScope(Dispatchers.Default).launch {
        try {
            val myList = repository.searchAsync(
                getUserId(),
                System.currentTimeMillis() / 1000 - 7*60*60
            ).await()

            myListLiveData.postValue(myList)
        } catch (t: Throwable) {
            t.printStackTrace()
            errorLiveData.postValue(Event(t.message ?:
            (getApplication() as Context).resources.getString(R.string.error)))
        }
    }
}