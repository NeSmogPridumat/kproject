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
import com.dteam.kproject.database.AppDataBase
import com.dteam.kproject.timeTable.repository.TimetablesRepository
import kotlinx.coroutines.*
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
            if(!check(date)) {
                if (!checkIsMy) {
                    val format = SimpleDateFormat("yyyy-MM-dd")
                    val formatDate = format.format(date)
                    repository.setTimesAsync(SetTimesData(getUserId(), formatDate, position))
                        .await()
                } else replace(date, position)

                getTimetables(date / 1000)
                setAlarm(date, position)
            } else {
                errorLiveData.postValue(
                    Event((getApplication() as Context).resources
                        .getString(R.string.unfortunately_you_can_borrow_once_every_two_days))
                )
            }
        } catch (t: Throwable){
            t.printStackTrace()
            errorLiveData.postValue(Event(t.message ?:
                    (getApplication() as Context).resources.getString(R.string.error)))
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setAlarm(date: Long, position: Int) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        calendar.set(Calendar.HOUR_OF_DAY, 9 + position - 1)
        calendar.set(Calendar.MINUTE, 50)
        val alarmManager =
            (getApplication() as Context).getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = createIntent()
        val pendingIntent = PendingIntent
            .getBroadcast(getApplication(), calendar.get(Calendar.DAY_OF_YEAR), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent )
        } else alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent )
    }

    private fun cancelAlarm(date: Long, position: Int){
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        calendar.set(Calendar.HOUR_OF_DAY, 9 + position - 1)
        calendar.set(Calendar.MINUTE, 50)
        val intent = createIntent()
        val pendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            calendar.get(Calendar.DAY_OF_YEAR),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager =
            (getApplication() as Context).getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    private fun createIntent():Intent {
        return Intent(getApplication(), NotificationReceiver::class.java)
    }

    @SuppressLint("SimpleDateFormat")
    fun delete(date: Long, position: Int) = CoroutineScope(Dispatchers.Default).launch {
        try {
            val format = SimpleDateFormat("yyyy-MM-dd")
            val formatDate = format.format(date)
            val answer = repository
                .deleteAsync(SetTimesData(getUserId(), formatDate, position) ).await()
            deleteLiveData.postValue(Event(answer))
            cancelAlarm(date, position)
        } catch (t:Throwable){
            t.printStackTrace()
            errorLiveData.postValue(Event(t.message ?:
                    (getApplication() as Context).resources.getString(R.string.error)))
        }
    }

    private fun getUserId(): String{
        return (getApplication() as Context).getSharedPreferences(
            MainActivity.preferenceKey, Context.MODE_PRIVATE)
            .getString(MainActivity.userIdKey, "")!!
    }

    fun search () = CoroutineScope(Dispatchers.Default).launch {
        try {
            val calendar = Calendar.getInstance()
            calendar.timeZone = TimeZone.getDefault()
            val myList = repository.searchAsync(
                getUserId(),
                (Calendar.getInstance().timeInMillis / 1000)
            ).await()

            myListLiveData.postValue(myList)

            for(myTimetable in myList){
                repository.saveInDB(myTimetable)
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            errorLiveData.postValue(Event(t.message ?:
            (getApplication() as Context).resources.getString(R.string.error)))
        }
    }

    fun clearDB()= CoroutineScope(Dispatchers.Default).launch {
        AppDataBase.getAppDataBase(getApplication())!!.clearAllTables()
        AppDataBase.destroyDataBase()
    }

    @SuppressLint("SimpleDateFormat")
    private suspend fun replace (date: Long, position: Int){
        val myTime = timetableLiveData.value?.positions?.first { it.user.id == getUserId() }
        myTime?.let {
            if (myTime.timeStart > Calendar.getInstance().timeInMillis / 1000) {
                val format = SimpleDateFormat("yyyy-MM-dd")
                //delete
                val deleteDate = format.format(myTime.timeStart * 1000)
                repository
                    .deleteAsync(SetTimesData(getUserId(), deleteDate, myTime.id)).await()

                //set
                val setDate = format.format(date)
                repository.setTimesAsync(SetTimesData(getUserId(), setDate, position)).await()
                errorLiveData.postValue(
                    Event((getApplication() as Context)
                        .resources.getString(R.string.your_turn_has_been_overwritten))
                )
                getTimetables(date / 1000)
                setAlarm(date, position)
            } else {
                errorLiveData.postValue(
                    Event((getApplication() as Context)
                        .resources.getString(R.string.sorry_your_time_is_up))
                )
            }
        }
    }

    private suspend fun check(todayTime: Long): Boolean{
        val todayCalendar = Calendar.getInstance()
        todayCalendar.timeInMillis = todayTime
        val yesterdayAndTomorrowPair = getYesterdayAndTomorrow(todayCalendar)
        val myQueues = ArrayList<Deferred<Boolean>>()
        myQueues.add(checkMyTimetableAsync(yesterdayAndTomorrowPair.first))
        myQueues.add(checkMyTimetableAsync(yesterdayAndTomorrowPair.second))
        return myQueues.awaitAll().contains(true)
    }

    private fun getYesterdayAndTomorrow(todayCalendar: Calendar): Pair<Calendar, Calendar> {
        val tomorrowCalendar = Calendar.getInstance()
        val yesterdayCalendar = Calendar.getInstance()

        yesterdayCalendar.set(Calendar.YEAR,
            todayCalendar.get(Calendar.YEAR))

        tomorrowCalendar.set(Calendar.YEAR,
            todayCalendar.get(Calendar.YEAR))

        when (todayCalendar.get(Calendar.DAY_OF_WEEK)){
            Calendar.MONDAY -> {
                yesterdayCalendar.set(Calendar.DAY_OF_YEAR,
                    todayCalendar.get(Calendar.DAY_OF_YEAR) - 3)

                tomorrowCalendar.set(Calendar.DAY_OF_YEAR,
                    todayCalendar.get(Calendar.DAY_OF_YEAR) + 1)
            }

            Calendar.FRIDAY -> {
                yesterdayCalendar.set(Calendar.DAY_OF_YEAR,
                    todayCalendar.get(Calendar.DAY_OF_YEAR) - 1)

                tomorrowCalendar.set(Calendar.DAY_OF_YEAR,
                    todayCalendar.get(Calendar.DAY_OF_YEAR) + 3)
            }

            else -> {
                yesterdayCalendar.set(Calendar.DAY_OF_YEAR,
                    todayCalendar.get(Calendar.DAY_OF_YEAR) - 1)

                tomorrowCalendar.set(Calendar.DAY_OF_YEAR,
                    todayCalendar.get(Calendar.DAY_OF_YEAR) + 1)
            }
        }
        return yesterdayCalendar to tomorrowCalendar
    }

    //если true = занято, false = не занято
    private fun checkMyTimetableAsync(calendar: Calendar):Deferred<Boolean>
            = CoroutineScope(Dispatchers.Default).async{
        val timetable = repository
            .getTimeTablesAsync(calendar.timeInMillis/1000).await()
        val my = timetable.positions.firstOrNull { it.user.id == getUserId() }
        return@async my != null
    }
}