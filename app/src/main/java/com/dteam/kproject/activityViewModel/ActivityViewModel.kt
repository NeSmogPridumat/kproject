package com.dteam.kproject.activityViewModel

import android.app.Application
import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.dteam.kproject.MainActivity
import com.dteam.kproject.authorization.repository.AuthRepository
import com.dteam.kproject.data.Event
import com.dteam.kproject.data.UserIdResponse
import com.dteam.kproject.database.AppDataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActivityViewModel @ViewModelInject constructor(
    application: Application,
    private val authRepository: AuthRepository
) : AndroidViewModel(application){

    private val checkLiveData by lazy {
        MutableLiveData<Event<Boolean>>()
    }
    fun getCheckLD() = checkLiveData

    fun check() = CoroutineScope(Dispatchers.Default).launch {
        try {
            val preference = (getApplication() as Context)
                .getSharedPreferences(MainActivity.preferenceKey, Context.MODE_PRIVATE)
            val userId = (preference.getString(MainActivity.userIdKey, ""))
            if (!userId.isNullOrEmpty()) {
                authRepository.checkAsync(
                    UserIdResponse((preference.getString(MainActivity.userIdKey, "")!!))
                ).await()
                checkLiveData.postValue(Event(true))
            } else checkLiveData.postValue(Event(false))
        } catch (t: Throwable) {
            t.printStackTrace()
            clearPreferences()
            clearDB()
            checkLiveData.postValue(Event(false))
        }
    }

    private fun clearDB()= CoroutineScope(Dispatchers.Default).launch {
        AppDataBase.getAppDataBase(getApplication())!!.clearAllTables()
        AppDataBase.destroyDataBase()
    }

    private fun clearPreferences() = CoroutineScope(Dispatchers.Default).launch {
        (getApplication() as Context).getSharedPreferences(
            MainActivity.preferenceKey, Context.MODE_PRIVATE
        ).edit().clear().apply()
    }
}