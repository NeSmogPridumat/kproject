package com.dteam.kproject.authorization.viewModel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dteam.kproject.data.Event
import com.dteam.kproject.authorization.repository.AuthRepository
import com.dteam.kproject.data.AuthorizationData
import com.dteam.kproject.data.RegisterData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AuthorizationViewModel @ViewModelInject constructor(
    application: Application,
    private val repository: AuthRepository
): AndroidViewModel(application) {

    //LiveData для регитрации результата
    private val checkLiveData by lazy {
        MutableLiveData<Event<Any>>()
    }
    fun getCheckLD(): LiveData<Event<Any>> = checkLiveData

    //LiveData для отображения ошибок
    private val errorLiveData by lazy {
        MutableLiveData<Event<String>>()
    }
    fun getErrorLiveData(): LiveData<Event<String>> = errorLiveData

    fun authorization(phone: String, password: String)
            = CoroutineScope(Dispatchers.Default).launch{
        try {
            val userIdResponse = repository
                .authorizationAsync(AuthorizationData(phone, password)).await()
            repository.saveUserId(userIdResponse.id, getApplication())
            checkLiveData.postValue(Event(userIdResponse.id))
        } catch (t:Throwable) {
            t.printStackTrace()
            errorLiveData.postValue(Event(t.message?:"Ошибка"))
        }
    }

    fun registration(phone: String, name: String, password: String)
            = CoroutineScope(Dispatchers.IO).launch{
        try {
            val userIdResponse = repository
                .registrationAsync(RegisterData(phone, name, password)).await()
            repository.saveUserId(userIdResponse.id, getApplication())
            checkLiveData.postValue(Event(userIdResponse.id))
        } catch (t: Throwable) {
            t.printStackTrace()
            errorLiveData.postValue(Event(t.message?:"Ошибка"))
        }
    }
}